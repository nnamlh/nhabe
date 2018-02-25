using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;

namespace MattanaSite.Controllers
{

    [Authorize(Roles = "Admin")]
    public abstract class MainController : Controller
    {

        public abstract List<SubMenuInfo> Menu(int idxActive);

        protected void AddMenu(int idxActive)
        {
            ViewBag.SubMenu = Menu(idxActive);
        }

	}
}