using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using MattanaSite.Models;
using System.IO;
using OfficeOpenXml;

namespace MattanaSite.Controllers
{
    public class AgencyController : MainController
    {
        MDBEntities db = new MDBEntities();

        //
        // GET: /Agency/
        [HttpGet]
        public ActionResult Show(int? page, string search)
        {
            AddMenu(0);

            int pageSize = 20;
            int pageNumber = (page ?? 1);

            if (String.IsNullOrEmpty(search))
                search = "";

            ViewBag.SearchText = search;

            var agency = db.MAgencies.Where(p => (p.Code.Contains(search) || p.Store.Contains(search)) && p.IsLock != 1).OrderByDescending(p => p.Code).ToPagedList(pageNumber, pageSize);

            return View(agency);
        }

        // them nhan vien
        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            return View(new MAgency());
        }

        [HttpGet]
        public ActionResult AddExcel()
        {
            AddMenu(2);

            return View();
        }

        [HttpPost]
        public ActionResult Add(MAgency info)
        {
            AddMenu(1);

            var check = db.MAgencies.Where(p => p.Code == info.Code).FirstOrDefault();

            if (check != null)
            {
                ViewBag.MSG = "Đại lý đã tồn tại";
                return View(check);
            }


            info.IsLock = 0;
            info.Id = Guid.NewGuid().ToString();

            db.MAgencies.Add(info);
            db.SaveChanges();


            return RedirectToAction("show", "agency");
        }

        [HttpGet]
        public ActionResult Delete(string id)
        {
            var check = db.MAgencies.Find(id);

            if (check == null)
                return Redirect("/error");
            return View(check);

        }

        [HttpPost]
        public ActionResult Delete(string id, string comfir)
        {
            var check = db.MAgencies.Find(id);

            if (check == null)
                return Redirect("/error");

            if (comfir == "ok")
            {
                check.IsLock = 1;
                db.Entry(check).State = System.Data.Entity.EntityState.Modified;
                db.SaveChanges();
            }

            return RedirectToAction("show", "agency");
        }

        [HttpGet]
        public ActionResult Modify(string id)
        {
            var check = db.MAgencies.Find(id);

            if (check == null)
                return Redirect("/error");
            return View(check);
        }


        [HttpPost]
        public ActionResult Modify(MAgency info)
        {
            var check = db.MAgencies.Find(info.Id);

            if (check == null)
                return Redirect("/error");


            check.Code = info.Code;
            check.Store = info.Store;
            check.Phone = info.Phone;
            check.AddressDetail = info.AddressDetail;
            check.IdentityCard = info.IdentityCard;
            check.Lat = info.Lat;
            check.Lng = info.Lng;
            check.Discount = info.Discount;
            check.Province = info.Province;

            db.Entry(check).State = System.Data.Entity.EntityState.Modified;
            db.SaveChanges();

            return View(check);
        }


        public override List<SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem danh sách",
                Url = "/agency/show",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Thêm đại lý",
                Url = "/agency/add",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Thêm bằng Excel",
                Url = "/agency/addexcel",
                Active = 0
            });

            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }


        [HttpPost]
        public ActionResult AddExcel(HttpPostedFileBase files)
        {
            AddMenu(2);

            string extension = System.IO.Path.GetExtension(files.FileName);
            if (!extension.Equals(".xlsx"))
                return Redirect("/error");

            string fileSave = "agency_" + DateTime.Now.ToString("ddMMyyyyhhmmss") + extension;
            string path = Server.MapPath("~/temp/" + fileSave);
            if (System.IO.File.Exists(path))
            {
                System.IO.File.Delete(path);
            }

            files.SaveAs(path);
            FileInfo newFile = new FileInfo(path);
            var package = new ExcelPackage(newFile);
            ExcelWorksheet sheet = package.Workbook.Worksheets[1];

            int totalRows = sheet.Dimension.End.Row;
            int totalCols = sheet.Dimension.End.Column;

            var listError = new List<MProduct>();

            for (int i = 2; i <= totalRows; i++)
            {

                try
                {
                    string name = Convert.ToString(sheet.Cells[i, 2].Value).Trim();
                    string code = Convert.ToString(sheet.Cells[i, 1].Value);
                    string address = Convert.ToString(sheet.Cells[i, 3].Value);
                    string province = Convert.ToString(sheet.Cells[i, 4].Value);

                    var check = db.MAgencies.Where(p => p.Code == code).FirstOrDefault();

                    if (check != null)
                    {
                        check.Store = name;
                        check.AddressDetail = address;
                        check.Province = province;
                        db.Entry(check).State = System.Data.Entity.EntityState.Modified;
                        db.SaveChanges();
                    }
                    else
                    {
                        var agencyNew = new MAgency()
                        {
                            Id = Guid.NewGuid().ToString(),
                            IsLock = 0,
                            Store = name,
                            AddressDetail = address,
                            Province = province,
                            Code = code,
                            Discount = 0,
                            Lat = 0,
                            Lng = 0

                        };

                        db.MAgencies.Add(agencyNew);
                        db.SaveChanges();
                    }

                }
                catch
                {

                }

            }

            return RedirectToAction("importexcel", "product");
        }
    }
}