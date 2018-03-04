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
            ViewBag.PType = db.ProductTypes.ToList();
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
            ViewBag.PType = db.ProductTypes.ToList();

            info.Id = Guid.NewGuid().ToString();

            db.MProducts.Add(info);
            db.SaveChanges();


            return View(new MProduct());
        }

        [HttpGet]
        public ActionResult Modify(string id)
        {
            var check = db.MProducts.Find(id);

            if (check == null)
                return Redirect("/error");

            ViewBag.PType = db.ProductTypes.ToList();

            return View(check);
        }

        [HttpPost]
        public ActionResult Modify(MProduct info)
        {
            var check = db.MProducts.Find(info.Id);

            if (check == null)
                return Redirect("/error");

            check.PCode = info.PCode;
            check.PName = info.PName;

            check.Price = info.Price;
            check.Size = info.Size;
            check.Describes = info.Describes;
            check.TypeId = info.TypeId;

            db.Entry(check).State = System.Data.Entity.EntityState.Modified;
            db.SaveChanges();

            ViewBag.PType = db.ProductTypes.ToList();

            return View(check);
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
            menues.Add(new SubMenuInfo()
            {
                Name = "Phân loại sản phẩm",
                Url = "/product/showtype",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;

        }

        public ActionResult ShowType()
        {
            AddMenu(2);
            return View(db.ProductTypes.ToList());
        }

        [HttpPost]
        public ActionResult AddType(string code, string name)
        {
            var check = db.ProductTypes.Find(code);

            if (check == null)
            {
                var newType = new ProductType()
                {
                    Id = code,
                    Name = name
                };
                db.ProductTypes.Add(newType);
                db.SaveChanges();
            }

            return RedirectToAction("showtype", "product");
        }

        [HttpPost]
        public ActionResult ModifyType(string Id, string Name)
        {
            var check = db.ProductTypes.Find(Id);

            if (check != null)
            {
                check.Name = Name;

                db.Entry(check).State = System.Data.Entity.EntityState.Modified;
                db.SaveChanges();
            }

            return RedirectToAction("showtype", "product");

        }
    }
}