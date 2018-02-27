using MattanaSite.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Util;

namespace MattanaSite.Controllers
{
    public class TrackController : MainController
    {

        MDBEntities db = new MDBEntities();
        //
        // GET: /Track/
        [HttpGet]
        public ActionResult Show()
        {
            AddMenu(0);

            return View();
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Vị trí hiện tại",
                Url = "/",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem lịch sử",
                Url = "/",
                Active = 0
            });


            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }
    }
}