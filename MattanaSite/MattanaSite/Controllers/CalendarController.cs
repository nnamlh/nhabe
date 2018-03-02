using MattanaSite.Models;
using OfficeOpenXml;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PagedList;

namespace MattanaSite.Controllers
{
    public class CalendarController : MainController
    {

        MDBEntities db = new MDBEntities();
        //
        // GET: /Calendar/
        [HttpGet]
        public ActionResult Show(int? page, string staffId = "", string fdate = "", string tdate = "", string status = "create")
        {
            AddMenu(0);


            int pageSize = 30;
            int pageNumber = (page ?? 1);

            ViewBag.Staff = db.MStaffs.ToList();

            DateTime fromDate;

            DateTime toDate;

            if (String.IsNullOrEmpty(fdate) || String.IsNullOrEmpty(tdate))
            {
                var currentDate = DateTime.Now;
                fromDate = currentDate;
                toDate = currentDate.AddDays(5);
            }
            else
            {
                fromDate = DateTime.ParseExact(fdate, "dd/MM/yyyy", null);
                toDate = DateTime.ParseExact(tdate, "dd/MM/yyyy", null);
            }

            ViewBag.FDate = fromDate;
            ViewBag.TDate = toDate;

            //  var calendars = db.CalendarWithStaffs.Where(p => p.StaffId.Contains(staffId)).Select(p => p.CalendarInfo).OrderByDescending(p => p.FDate).ToList();

            var data = (from log in db.CalendarInfoes
                        where (DbFunctions.TruncateTime(log.FDate)
                                           >= DbFunctions.TruncateTime(fromDate) && DbFunctions.TruncateTime(log.FDate)
                                           <= DbFunctions.TruncateTime(toDate))
                        select log).ToList();



            var staff = db.MStaffs.Find(staffId);
            if (staff != null)
                return View(data.Where(p => p.MStaffs.Contains(staff)).OrderByDescending(p => p.FDate).ToPagedList(pageNumber, pageSize));

            return View(data.OrderByDescending(p => p.FDate).ToPagedList(pageNumber, pageSize));
        }

        [HttpPost]
        public ActionResult Remove(string id)
        {
            var result = db.delete_calendar(id);

            return RedirectToAction("show", "calendar");
        }

        [HttpGet]
        public ActionResult ShowDetail(string id)
        {

            ViewBag.CStaff = db.CalendarInfoes.Find(id);

            var detail = db.get_detail_calendar_by_calendarid(id).ToList();

            return View(detail);
        }

        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            var currentDate = DateTime.Now;
            ViewBag.FDate = currentDate;
            ViewBag.TDate = currentDate.AddDays(5);


            ViewBag.Staff = db.MStaffs.ToList();

            return View();
        }

        [HttpPost]
        public ActionResult Add(string fdate, string tdate, HttpPostedFileBase files, List<string> staffChoose)
        {
            AddMenu(1);

            ViewBag.Staff = db.MStaffs.ToList();


            var fromDate = DateTime.ParseExact(fdate, "dd/MM/yyyy", null);
            var toDate = DateTime.ParseExact(tdate, "dd/MM/yyyy", null);


            ViewBag.FDate = fromDate;
            ViewBag.TDate = toDate;


            int compare = DateTime.Compare(fromDate, toDate);
            if (compare >= 0)
                return Redirect("/error");


            // var allDates = DateRange(fromDate, toDate);
            if (staffChoose == null || staffChoose.Count() == 0)
            {
                ViewBag.MSG = "Thiếu nhân viên";
                return View();
            }

            if (files == null)
                return Redirect("/error");

            string extension = System.IO.Path.GetExtension(files.FileName);
            if (!extension.Equals(".xlsx"))
                return Redirect("/error");

            var calendarInfo = new CalendarInfo()
            {
                Id = Guid.NewGuid().ToString(),
                TDate = toDate,
                FDate = fromDate,
                IsLock = 0,
                WeekOfYear = GetIso8601WeekOfYear(fromDate)
            };

            db.CalendarInfoes.Add(calendarInfo);
            db.SaveChanges();

            // them nhan vien
            foreach (var item in staffChoose)
            {
                var staff = db.MStaffs.Find(item);


                if (staff != null)
                {
                    calendarInfo.MStaffs.Add(staff);


                }
                db.SaveChanges();
            }



            string fileSave = "calenadar_" + DateTime.Now.ToString("ddMMyyyyhhmmss") + extension;
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

            for (int i = 2; i <= totalRows; i++)
            {
                string code = Convert.ToString(sheet.Cells[i, 1].Value);

                string cDay = Convert.ToString(sheet.Cells[i, 3].Value);

                string cMonth = Convert.ToString(sheet.Cells[i, 4].Value);

                string cYear = Convert.ToString(sheet.Cells[i, 5].Value);

                var findAgency = db.MAgencies.Where(p => p.Code == code).FirstOrDefault();

                if (findAgency != null)
                {
                    var calendarWork = new CalendarWork()
                      {
                          AgencyId = findAgency.Id,
                          CDay = Convert.ToInt32(cDay),
                          CDate = "D" + cDay,
                          CMonth = Convert.ToInt32(cMonth),
                          CYear = Convert.ToInt32(cYear),
                          InPlan = 1,
                          Perform = 0,
                          DayInWeek = GetDayOfWeek(Convert.ToInt32(cDay), Convert.ToInt32(cMonth), Convert.ToInt32(cYear)),
                          Id = Guid.NewGuid().ToString(),
                          TypeId = "CSBH",
                          CalendarId = calendarInfo.Id
                      };

                    db.CalendarWorks.Add(calendarWork);
                    db.SaveChanges();
                }

            }

            return View();
        }

        public override List<Models.SubMenuInfo> Menu(int idxActive)
        {
            List<SubMenuInfo> menues = new List<SubMenuInfo>();

            menues.Add(new SubMenuInfo()
            {
                Name = "Xem lịch",
                Url = "/calendar/show",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Thêm lịch",
                Url = "/calendar/add",
                Active = 0
            });

            menues.Add(new SubMenuInfo()
            {
                Name = "Báo cáo lịch",
                Url = "/calendar/report",
                Active = 0
            });

            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }


    }
}