using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;
using MattanaSite.Util;
using System.Net;
using System.IO;

namespace MattanaSite.Controllers
{
    public class NotificationController : MainController
    {
        MDBEntities db = new MDBEntities();
        MongoHelper mongoHelp = new MongoHelper();
        //
        // GET: /Notification/
        [Authorize]
        public ActionResult Send()
        {


            return View(db.MStaffs.ToList());
        }

        [HttpPost]
        public ActionResult Send(string title, string messenge,  string user)
        {

            var firebaseId = "";

            if (user == "all")
            {
                firebaseId = "/topics/golobal";
            }
            else
            {
                firebaseId = mongoHelp.findFirebaseId(user);

                if (firebaseId == "")
                {
                    ViewBag.MSG = "Nhân viên chưa sử dụng APP";
                    return View(db.MStaffs.ToList());
                }

            }

           
            title = title.ToUpper();


            string json = "{ \"notification\": {\"click_action\": \"OPEN_ACTIVITY_1\" ,\"title\": \"" + title + "\",\"body\": \"" + messenge + "\"},\"data\": {\"title\": \"'" + title + "'\",\"message\": \"'" + messenge + "'\"},\"to\": \"" + firebaseId + "\"}";

            var responseString = Utils.sendRequestFirebase(json);

            mongoHelp.saveNoticeHistory(user,title, messenge);

            ViewBag.MSG = "Đã gửi";
            return View(db.MStaffs.ToList());
        }


        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {
            return null;
        }

        
    }
}