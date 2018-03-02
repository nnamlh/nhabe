using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;
using MattanaSite.Models;

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

            var agency = db.MAgencies.Where(p => p.Code.Contains(search) || p.Store.Contains(search)).OrderByDescending(p => p.Code).ToPagedList(pageNumber, pageSize);

            return View(agency);
        }

        // them nhan vien
        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            ViewBag.Area = db.AreaInfoes.ToList();

            return View(new MAgency());
        }


        [HttpPost]
        public ActionResult Add(MAgency info)
        {
            AddMenu(1);

            ViewBag.Area = db.AreaInfoes.ToList();

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
        public ActionResult Modify(string id)
        {
            var check = db.MAgencies.Find(id);

            if (check == null)
                return Redirect("/error");
            ViewBag.Area = db.AreaInfoes.ToList();
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
            check.Deputy = info.Deputy;
            check.AreaId = info.AreaId;
            check.AddressDetail = info.AddressDetail;
            check.IdentityCard = info.IdentityCard;
            check.Lat = info.Lat;
            check.Lng = info.Lng;

            db.Entry(check).State = System.Data.Entity.EntityState.Modified;
            db.SaveChanges();


            ViewBag.Area = db.AreaInfoes.ToList();
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


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }
    }
}