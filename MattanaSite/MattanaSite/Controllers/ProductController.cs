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