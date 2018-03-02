using MattanaSite.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Util;
using System.Web.Script.Serialization;

namespace MattanaSite.Controllers
{
    public class TrackController : MainController
    {
        MongoHelper mongoHelper = new MongoHelper();
        MDBEntities db = new MDBEntities();
        //
        // GET: /Track/
        [HttpGet]
        public ActionResult Show()
        {
            AddMenu(0);



            return View();
        }

        [HttpGet]
        public ActionResult Tracking(string user, int day = 0, int month = 0, int year = 0)
        {
            AddMenu(1);

            ViewBag.Staff = db.MStaffs.ToList();

            if (month == 0)
                month = DateTime.Now.Month;

            if (year == 0)
                year = DateTime.Now.Year;

            if (day == 0)
                day = DateTime.Now.Day;

            ViewBag.Month = month;
            ViewBag.Year = year;
            ViewBag.Day = day;

            ViewBag.User = user;

            DateTime date = new DateTime(year, month, day, 0, 0, 0, DateTimeKind.Utc);
            

            var data = mongoHelper.ShowLocationStaff(user, date).Select(p => new LocationInfo()
            {
                lat = p.Lat,
                lng = p.Lng
            }).ToArray(); ;
       
            

           // var jsonSerialiser = new JavaScriptSerializer();
            ViewBag.Data = data;

            return View();
        }

        [HttpGet]
        public ActionResult UpdateNewLocation(string user)
        {

            var data = mongoHelper.findNewLocationStaff(user);

            if (data == null)
                return Json(new {id = 0}, JsonRequestBehavior.AllowGet);

            return Json(new { lat = data.Lat, lng = data.Lng , id = 1 }, JsonRequestBehavior.AllowGet);
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Vị trí hiện tại",
                Url = "/track/show",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem lịch sử",
                Url = "/track/tracking",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }
    }
}