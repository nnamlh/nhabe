using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MATTANAAPI.Models;
using MATTANAAPI.Util;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;

namespace MATTANAAPI.Controllers
{
    public class BaseController : ApiController
    {
        protected MDBEntities db;
        protected MongoHelper mongoHelper;

        private Dictionary<DayOfWeek, string> mapDayOfWeeks;
        public BaseController()
        {
            db = new MDBEntities();
            mongoHelper = new MongoHelper();
            mapDayOfWeeks = new Dictionary<DayOfWeek, string>();

            mapDayOfWeeks[DayOfWeek.Monday] = "T2";
            mapDayOfWeeks[DayOfWeek.Tuesday] = "T3";
            mapDayOfWeeks[DayOfWeek.Wednesday] = "T4";
            mapDayOfWeeks[DayOfWeek.Thursday] = "T5";
            mapDayOfWeeks[DayOfWeek.Friday] = "T6";
            mapDayOfWeeks[DayOfWeek.Saturday] = "T7";
            mapDayOfWeeks[DayOfWeek.Sunday] = "CN";


        }

        protected bool isAdmin(string user)
        {
            var checkUser = db.AspNetUsers.Where(p => p.UserName == user).FirstOrDefault();

            var role = checkUser.AspNetRoles.FirstOrDefault();

            if (role == null)
                return false;

            if (role.Name.Equals("Admin"))
                return true;

            return false;
        }

        protected UserManager<ApplicationUser> UserManager = new UserManager<ApplicationUser>(new UserStore<ApplicationUser>(new ApplicationDbContext()));

    }
}
