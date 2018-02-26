using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;
using PagedList;

namespace MattanaSite.Controllers
{
    public class ProductController : MainController
    {

        MDBEntities db = new MDBEntities();
        //
        // GET: /Product/
        public ActionResult Show(int? page, string search)
        {
            AddMenu(0);

            int pageSize = 20;
            int pageNumber = (page ?? 1);

            if (String.IsNullOrEmpty(search))
                search = "";

            ViewBag.SearchText = search;

            var product = db.MProducts.Where(p => p.PCode.Contains(search) || p.PName.Contains(search)).OrderBy(p => p.PName).ToPagedList(pageNumber, pageSize);

            return View(product);
        }

        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            return View(new MProduct());
        }

        [HttpPost]
        public ActionResult Add(MProduct info)
        {
            AddMenu(1);

            var check = db.MProducts.Where(p => p.PCode == info.PCode).FirstOrDefault();

            if (check != null)
            {
                ViewBag.MSG = "Mã đã tồn tại";
                return View(info);
            }

            info.Id = Guid.NewGuid().ToString();

            db.MProducts.Add(info);
            db.SaveChanges();


            return View(new MProduct());
        }

        public override List<SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem danh sách",
                Url = "/product/show",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Thêm sản phẩm",
                Url = "/product/add",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;

        }
    }
}