using MattanaSite.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;

namespace MattanaSite.Controllers
{

    public class StaffController : MainController
    {
        MDBEntities db = new MDBEntities();

        //
        // GET: /Staff/
        [HttpGet]
        public ActionResult Show(int? page, string search)
        {
            AddMenu(0);

            int pageSize = 20;
            int pageNumber = (page ?? 1);

            if (String.IsNullOrEmpty(search))
                search = "";

            ViewBag.SearchText = search;

            var staff = db.MStaffs.Where(p => p.Id.Contains(search) || p.FullName.Contains(search)).OrderByDescending(p => p.Id).ToPagedList(pageNumber, pageSize);

            return View(staff);
        }


        // them nhan vien
        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            return View(new MStaff());
        }


        [HttpPost]
        public ActionResult Add(MStaff staff)
        {
            AddMenu(1);

            var check = db.MStaffs.Find(staff.Id);

            if (check != null)
            {
                ViewBag.MSG = "Mã nhân viên đã tồn tại";

                return View(staff);
            }

            staff.IsLock = 0;

            db.MStaffs.Add(staff);

            db.SaveChanges();

            return RedirectToAction("show", "staff");
        }


        public override List<SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem danh sách",
                Url = "/staff/show",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Thêm nhân viên",
                Url = "/staff/add",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;

        }


        [HttpGet]
        public ActionResult Modify(string id)
        {
            var check = db.MStaffs.Find(id);

            if (check == null)
                return Redirect("/error");


            ViewBag.Staff = check;

            return View();
        }


        [HttpPost]
        public ActionResult Modify(MStaff staff, string Lock)
        {
            var check = db.MStaffs.Find(staff.Id);

            if (check == null)
                return Redirect("/error");


            check.FullName = staff.FullName;

            check.IdentityCard = staff.IdentityCard;

            if (Lock == null)
                check.IsLock = 0;
            else
                check.IsLock = 1;


            check.Phone = staff.Phone;

            check.GroupNumber = staff.GroupNumber;

            db.Entry(check).State = System.Data.Entity.EntityState.Modified;

            db.SaveChanges();

            return Redirect("/staff/modify/" + check.Id);
        }
    }
}