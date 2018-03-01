using MattanaSite.Models;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;

namespace MattanaSite.Controllers
{
    public class ManageController : MainController
    {

        MDBEntities db = new MDBEntities();

        
        private ApplicationDbContext sdb = new ApplicationDbContext();

        public RoleManager<IdentityRole> RoleManager { get; private set; }

        public UserManager<ApplicationUser> UserManager { get; private set; }

        public ManageController()
        {
            RoleManager = new RoleManager<IdentityRole>(new RoleStore<IdentityRole>(sdb));
            UserManager = new UserManager<ApplicationUser>(new UserStore<ApplicationUser>(sdb));
            UserManager.UserValidator = new UserValidator<ApplicationUser>(UserManager)
            {
                AllowOnlyAlphanumericUserNames = false

            };
        }

        //
        // GET: /Manage/
        public ActionResult MakeAdmin(int? page, string search)
        {
            AddMenu(0);
             int pageSize = 30;
            int pageNumber = (page ?? 1);
            var data = db.show_user_role().ToList();

            return View(data.OrderByDescending(p=> p.Id).ToPagedList(pageNumber, pageSize));
        }

        [HttpPost]
        public ActionResult UpdateRole(string userId, bool role)
        {

           var user = UserManager.FindById(userId);

           // var userRole = user.Roles.ToList();

           if (user.UserName == User.Identity.Name)
           {
               return Content("Not with yourself !");
           }

            try
            {
                if (!role)
                {
                    UserManager.RemoveFromRole(userId, "Admin");
                    return Content("The role was removed !");
                }

                UserManager.AddToRole(userId, "Admin");

                return Content("The role was added !");
            } catch
            {
                return Content("Fail");
            }
        }

        [HttpGet]
        public ActionResult ChangePass()
        {

            return View();
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Cấp quyền",
                Url = "/manage/makeadmin",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }
    }
}