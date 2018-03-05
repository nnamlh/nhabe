using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;
using PagedList;
using System.IO;
using OfficeOpenXml;

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

            var product = db.MProducts.Where(p => (p.PCode.Contains(search) || p.PName.Contains(search)) && p.IsLock != 1).OrderBy(p => p.PName).ToPagedList(pageNumber, pageSize);

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

            var check = db.MProducts.Where(p => p.PCode == info.PCode && p.PMainCode == info.PMainCode).FirstOrDefault();

            if (check != null)
            {
                ViewBag.MSG = "Mã đã tồn tại";
                return View(info);
            }
            ViewBag.PType = db.ProductTypes.ToList();

            info.Id = Guid.NewGuid().ToString();

            db.MProducts.Add(info);
            db.SaveChanges();
            ViewBag.MSG = "Đã thêm " + info.PName;

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
            check.PMainCode = info.PMainCode;
            check.Price = info.Price;
            check.PSize = info.PSize;
            check.TypeId = info.TypeId;

            db.Entry(check).State = System.Data.Entity.EntityState.Modified;
            db.SaveChanges();

            ViewBag.PType = db.ProductTypes.ToList();

            return View(check);
        }

        [HttpGet]
        public ActionResult Delete(string id)
        {
            var check = db.MProducts.Find(id);

            if (check == null)
                return Redirect("/error");

            return View(check);
        }

        [HttpPost]
        public ActionResult Delete(string id, string comfir)
        {
            var check = db.MProducts.Find(id);

            if (check == null)
                return Redirect("/error");

            if (comfir == "ok")
            {
                check.IsLock = 1;
                db.Entry(check).State = System.Data.Entity.EntityState.Modified;
                db.SaveChanges();
            }

            return RedirectToAction("show", "product"); ;
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
                Name = "Thêm SP từ Excel",
                Url = "/product/importexcel",
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
            AddMenu(3);
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


        [HttpGet]
        public ActionResult ImportExcel()
        {
            AddMenu(2);

            ViewBag.PType = db.ProductTypes.ToList();
            return View();
        }

        [HttpPost]
        public ActionResult ImportExcel(HttpPostedFileBase files, string TypeId)
        {
            AddMenu(2);

            string extension = System.IO.Path.GetExtension(files.FileName);
            if (!extension.Equals(".xlsx"))
                return Redirect("/error");

            string fileSave = "product_" + DateTime.Now.ToString("ddMMyyyyhhmmss") + extension;
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
                    string name = Convert.ToString(sheet.Cells[i, 2].Value);
                    string code = Convert.ToString(sheet.Cells[i, 3].Value);
                    string mainCode = Convert.ToString(sheet.Cells[i, 4].Value);
                    string size = Convert.ToString(sheet.Cells[i, 5].Value);
                    string price = Convert.ToString(sheet.Cells[i, 6].Value);

                    var product = new MProduct()
                    {
                        Id = Guid.NewGuid().ToString(),
                        IsLock = 0,
                        PCode = code,
                        PMainCode = mainCode,
                        PName = name,
                        PSize = size,
                        TypeId = TypeId,
                        Price = Convert.ToDouble(price)
                        
                    };

                    db.MProducts.Add(product);
                    db.SaveChanges();

                }catch
                {

                }

            }

            return RedirectToAction("importexcel", "product");
        }
    }
}