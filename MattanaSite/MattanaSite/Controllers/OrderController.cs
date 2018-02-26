using MattanaSite.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using System.Data.Entity;

namespace MattanaSite.Controllers
{
    public class OrderController : MainController
    {
        MDBEntities db = new MDBEntities();
        //
        // GET: /Order/
        [HttpGet]
        public ActionResult Show(int? page, string fdate, string tdate, string stt = "create", string staff = "")
        {
            DateTime fromDate;

            DateTime toDate;
            int pageSize = 30;
            int pageNumber = (page ?? 1);
            if (String.IsNullOrEmpty(fdate) || String.IsNullOrEmpty(tdate))
            {
                var currentDate = DateTime.Now;
                fromDate = new DateTime(currentDate.Year, currentDate.Month, 1);
                toDate = fromDate.AddDays(GetDaysInMonth(currentDate.Year, currentDate.Month));
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

            var data = (from log in db.MOrders
                        where DbFunctions.TruncateTime(log.CreateTime)
                                           >= DbFunctions.TruncateTime(fromDate) && DbFunctions.TruncateTime(log.CreateTime)
                                           <= DbFunctions.TruncateTime(toDate) && log.StatusId == stt && log.StatusId.Contains(staff)
                        select log).OrderByDescending(p => p.CreateTime).ToPagedList(pageNumber, pageSize);


            return View(data);
        }

        [HttpGet]
        public ActionResult ShowDetail(string id)
        {
            var check = db.MOrders.Find(id);

            if (check == null)
                return Redirect("/error");


            return View(check);
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {

           
            return null;


        }
    }
}