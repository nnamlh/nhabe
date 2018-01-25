using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;

namespace MattanaSite.Controllers
{

    public class HomeController : MainController
    {
        public ActionResult Index()
        {

            AddMenu(1);

            return View();
        }


        /*
        public override List<SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "tess",
                Url = "/",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "tess",
                Url = "",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "tess",
                Url = "",
                Active = 0
            });

            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;

        }
         * */

        public override List<SubMenuInfo> Menu(int idxActive)
        {
            return null;
        }
    }
}