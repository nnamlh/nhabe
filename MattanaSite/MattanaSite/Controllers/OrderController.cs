using MattanaSite.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using System.Data.Entity;
using MattanaSite.Util;
using System.IO;
using OfficeOpenXml;

namespace MattanaSite.Controllers
{
    public class OrderController : MainController
    {
        MDBEntities db = new MDBEntities();
        MongoHelper mongoHelp = new MongoHelper();
        //
        // GET: /Order/
        [HttpGet]
        public ActionResult Show(int? page, string fdate, string tdate, string stt = "all", string staff = "all", string OrderCode = "", string AgencyCode = "")
        {
            DateTime fromDate;

            DateTime toDate;
            int pageSize = 30;
            int pageNumber = (page ?? 1);
            if (String.IsNullOrEmpty(fdate) || String.IsNullOrEmpty(tdate))
            {
                toDate = DateTime.Now;
                fromDate = toDate.AddMonths(-1);
            }
            else
            {
                fromDate = DateTime.ParseExact(fdate, "dd/MM/yyyy", null);
                toDate = DateTime.ParseExact(tdate, "dd/MM/yyyy", null);
            }
            ViewBag.FDate = fromDate;
            ViewBag.TDate = toDate;
            ViewBag.Status = db.OrderStatus.ToList();
            ViewBag.CSTT = stt;
            ViewBag.StaffChoose = staff;
            ViewBag.Staff = db.MStaffs.ToList();
            ViewBag.AgencyCode = AgencyCode;

            var data = new List<MOrder>();

            if (!String.IsNullOrEmpty(OrderCode))
            {
                data = db.MOrders.Where(p => p.Code == OrderCode).ToList();
            }
            else
            {
                if (stt == "all")
                    stt = "";


                if (staff == "all")
                    staff = "";

                data = (from log in db.MOrders
                        where DbFunctions.TruncateTime(log.CreateTime)
                                           >= DbFunctions.TruncateTime(fromDate) && DbFunctions.TruncateTime(log.CreateTime)
                                           <= DbFunctions.TruncateTime(toDate) && log.StatusId.Contains(stt) && log.StaffId.Contains(staff)
                        select log).ToList();
            }


            return View(data.OrderByDescending(p=> p.CreateTime).ToPagedList(pageNumber, pageSize));
        }

        [HttpGet]
        public ActionResult ShowDetail(string id)
        {
            var check = db.MOrders.Find(id);

            if (check == null)
                return Redirect("/error");

            ViewBag.StatusAppove = db.OrderStatus.Where(p => p.PreStt == check.StatusId).FirstOrDefault();


            return View(check);
        }

        [HttpPost]
        public ActionResult Change(string orderId, string status)
        {
            var check = db.MOrders.Find(orderId);

            if (check == null)
                return Redirect("/error");

            check.ModifyTime = DateTime.Now;

            var checkStatus = db.OrderStatus.Find(status);

            check.StatusId = status;

            db.Entry(check).State = EntityState.Modified;

            db.SaveChanges();

            Utils.send(check.MStaff.MUser, "Đơn hàng", "Đơn hàng " + check.Code + " \nCủa đại lý: " + check.MAgency.Store + " \nĐã thay đổi trạng thái: " + checkStatus.Name, mongoHelp);

            return Redirect("/order/showdetail/" + orderId);
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {

           
            return null;


        }

        [HttpPost]
        public ActionResult UpdateDelivery(string productId, string orderId, int quantity)
        {
            if(quantity < 0)
                return Json(new { id = 0 }, JsonRequestBehavior.AllowGet);

            var check = db.ProductOrders.Where(p => p.ProductId == productId && p.OrderId == orderId).FirstOrDefault();

            if (check != null && check.MOrder.StatusId == "create")
            {
                check.QuantityReal = quantity;
                db.Entry(check).State = EntityState.Modified;
                db.SaveChanges();

                double? total = 0;

                var orders = db.MOrders.Find(orderId);

                foreach (var item in orders.ProductOrders)
                {
                    total += (item.QuantityReal * item.Price);
                }

                orders.PriceReal = total;
                db.Entry(orders).State = EntityState.Modified;
                db.SaveChanges();

                Util.Utils.send(orders.MStaff.MUser, "Đơn hàng " + orders.Code, "Đơn hàng " + orders.Code + "\nĐã thay đổi số lượng thực: " + quantity, mongoHelp);

                return Json(new { money = (check.QuantityReal * check.Price).Value.ToString("C", Cultures.VietNam), total = total.Value.ToString("C", Cultures.VietNam) }, JsonRequestBehavior.AllowGet);
            }

            return Json(new { id = 0 }, JsonRequestBehavior.AllowGet);
        }

        /**
         * xuat excel
         */
        public ActionResult ExcelOrderForm(string orderId)
        {
            var order = db.MOrders.Find(orderId);

            if(order == null)
                return Redirect("/error");

            string pathRoot = Server.MapPath("~/MTemplates/orderform.xlsx");
            string name = "orderform" + DateTime.Now.ToString("ddMMyyyyHHmmss") + ".xlsx";
            string pathTo = Server.MapPath("~/Temp/" + name);

            System.IO.File.Copy(pathRoot, pathTo);

            try
            {
                FileInfo newFile = new FileInfo(pathTo);
 
                using (ExcelPackage package = new ExcelPackage(newFile))
                {
                    ExcelWorksheet worksheet = package.Workbook.Worksheets["donhang"];

                    worksheet.Cells[3, 6].Value = order.Code;
                    worksheet.Cells[4, 6].Value = order.CreateTime.Value.ToString("dd/MM/yyyy");
                    worksheet.Cells[5, 6].Value = order.MAgency.AddressDetail;
                    worksheet.Cells[6, 6].Value = order.MAgency.Deputy;
                    worksheet.Cells[7, 6].Value = order.SuggestDate != null ? order.SuggestDate.Value.ToString("dd/MM/yyyy") : "";

                    worksheet.Cells[5, 3].Value = order.MStaff.FullName;
                    worksheet.Cells[6, 3].Value = order.MStaff.GroupNumber;
                    worksheet.Cells[7, 3].Value = order.MAgency.Code;

                    var products = order.ProductOrders.ToList();

                    for (int i = 0; i < products.Count(); i++)
                    {
                        worksheet.Cells[i + 13, 1].Value = i + 1;
                        worksheet.Cells[i + 13, 2].Value = products[i].MProduct.PName;
                        worksheet.Cells[i + 13, 3].Value = products[i].MProduct.PCode;
                        worksheet.Cells[i + 13, 5].Value = products[i].MProduct.PSize;
                        worksheet.Cells[i + 13, 6].Value = products[i].MProduct.Price;
                        worksheet.Cells[i + 13, 7].Value = products[i].Price;
                        worksheet.Cells[i + 13, 8].Value = products[i].QuantityBuy;
                        worksheet.Cells[i + 13, 9].Value = products[i].Price * products[i].QuantityBuy;
                        worksheet.Cells[i + 13, 10].Value = products[i].QuantityReal;
                        worksheet.Cells[i + 13, 11].Value = products[i].Price * products[i].QuantityReal;

                    }

                    package.Save();
                }

            }
            catch
            {
                return Redirect("/error");
            }


            return File(pathTo, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", string.Format("don_hang-" + DateTime.Now.ToString("ddMMyyyyhhmmss") + ".{0}", "xlsx"));
        }
    }
}