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
        public ActionResult Show(int? page, int? year, int? week, string staffId = "all")
        {
            AddMenu(0);

            int pageSize = 30;
            int pageNumber = (page ?? 1);

            ViewBag.Staff = db.MStaffs.ToList();

            if (week == null)
            {
                week = GetIso8601WeekOfYear(DateTime.Now);
                
            }

            if (year == null)
                year = DateTime.Now.Year;

            var firstWeekCreate = FirstDateOfWeekISO8601((int)year, (int)week);

            ViewBag.FDate = firstWeekCreate.ToString("dd/MM/yyyy");
            ViewBag.TDate = firstWeekCreate.AddDays(5).ToString("dd/MM/yyyy");

            ViewBag.Week = week;

            ViewBag.Year = DateTime.Now.Year;
           
            var data = new List<CalendarInfo>();

            ViewBag.StaffChoose = staffId;

            if (staffId == "all")
            {
                data = db.CalendarInfoes.Where(p => p.WeekOfYear == week && p.CYear == year).ToList();
            }
            else
            {
                data = db.CalendarInfoes.Where(p => p.WeekOfYear == week && p.CYear == year && p.StaffId == staffId).ToList();
            }

            return View(data.OrderByDescending(p => p.FDate).ToPagedList(pageNumber, pageSize));
        }

        [HttpPost]
        public ActionResult Remove(string id)
        {


            return RedirectToAction("show", "calendar");
        }
        
        [HttpGet]
        public ActionResult FindCalendarPlan(int week, int year, string staffId)
        {
            var check = db.CalendarInfoes.Where(p => p.WeekOfYear == week && p.CYear == year && p.StaffId == staffId).FirstOrDefault();

            if (check == null)
                return Content("Chưa có lịch tuần " + week + " năm " + year);

            return RedirectToAction("showdetail", "calendar", new { id = check.Id });
        }


        [HttpGet]
        public ActionResult ShowDetail(string id)
        {
            AddMenu(0);

            var check = db.CalendarInfoes.Find(id);

            if (check == null || check.CStatus != 0)
            {
                return Redirect("/error");
            }

            var startDate = DateTime.ParseExact(check.FDate, "dd/MM/yyyy", null);

            var endDate = DateTime.ParseExact(check.TDate, "dd/MM/yyyy", null);

            List<ShowCalendarDay> planTimes = new List<ShowCalendarDay>();

            for (DateTime date = startDate; date <= endDate; )
            {
                ShowCalendarDay data = new ShowCalendarDay()
                {
                    date = date.ToString("dd/MM/yyyy"),
                    code = date.ToString("ddMMyyyy"),
                    dayOfWeek = mapDayOfWeeks[date.DayOfWeek],
                    plan = new List<ShowCalendarAgency>(),
                    work = new List<ShowCalendarAgency>()

                };

                var planCode = date.ToString("ddMMyyyy");

                var listPlan = db.CalendarPlans.Where(p => p.CalendarId == check.Id && p.CDate == planCode).ToList();

                foreach (var item in listPlan)
                {
                    data.plan.Add(new ShowCalendarAgency()
                    {
                        id = item.MAgency.Id,
                        code = item.MAgency.Code,
                        name = item.MAgency.Store,
                        target = item.Targets.Value.ToString("C", Util.Cultures.VietNam)
                    });
                }

                var listWork = db.CalendarWorks.Where(p => p.StaffId == check.StaffId && p.CDate == planCode && p.Perform == 1).ToList();

                foreach (var item in listWork)
                {
                    data.work.Add(new ShowCalendarAgency()
                    {
                        id = item.MAgency.Id,
                        code = item.MAgency.Code,
                        name = item.MAgency.Store
                    });
                }


                planTimes.Add(data);

                date = date.AddDays(1);
            }

            ViewBag.PlanTime = planTimes;

            return View(check);
        }

        [HttpGet]
        public ActionResult Add()
        {
            AddMenu(1);

            int CurrentWeek = GetIso8601WeekOfYear(DateTime.Now);

            int weekWork = CurrentWeek + 1;

            ViewBag.Week = weekWork;

            ViewBag.Year = DateTime.Now.Year;

            ViewBag.CurrentWeek = CurrentWeek;

            var firstWeekCreate = FirstDateOfWeekISO8601(DateTime.Now.Year, weekWork);

            ViewBag.FDate = firstWeekCreate.ToString("dd/MM/yyyy");
            ViewBag.TDate = firstWeekCreate.AddDays(5).ToString("dd/MM/yyyy");

            ViewBag.Staff = db.MStaffs.ToList();

            return View();
        }

        [HttpGet]
        public ActionResult GetDateOfWeek(int week, int year)
        {
            var firstWeekCreate = FirstDateOfWeekISO8601(year, week);

            return Json(new { from = firstWeekCreate.ToString("dd/MM/yyyy"), to = firstWeekCreate.AddDays(5).ToString("dd/MM/yyyy") }, JsonRequestBehavior.AllowGet);

        }



        [HttpPost]
        public ActionResult Add(int week, int year, string staffChoose, string title)
        {

            int CurrentWeek = GetIso8601WeekOfYear(DateTime.Now);

            if (week <= 0 || week > 52 || week < CurrentWeek)
                return Redirect("/error");

            if (year < DateTime.Now.Year)
                return Redirect("/error");

            var firstWeekCreate = FirstDateOfWeekISO8601(year, week);

            var fDate = firstWeekCreate.ToString("dd/MM/yyyy");

            var tDate = firstWeekCreate.AddDays(5).ToString("dd/MM/yyyy");

            var staffCheck = db.MStaffs.Find(staffChoose);

            if (staffChoose == null)
                return Redirect("/error");

            var checkCalendar = db.CalendarInfoes.Where(p => p.WeekOfYear == week && p.CYear == year && p.StaffId == staffCheck.Id).FirstOrDefault();

            if (checkCalendar != null)
                return RedirectToAction("edit", "calendar", new { id = checkCalendar.Id });



            var calendarInfo = new CalendarInfo()
            {
                Id = Guid.NewGuid().ToString(),
                Title = title,
                WeekOfYear = week,
                CYear = year,
                CStatus = 0,
                CreateTine = DateTime.Now,
                FDate = fDate,
                TDate = tDate,
                StaffId = staffCheck.Id

            };

            db.CalendarInfoes.Add(calendarInfo);
            db.SaveChanges();

            return RedirectToAction("edit", "calendar", new { id = calendarInfo.Id });

        }

        public ActionResult Edit(string id)
        {
            AddMenu(1);

            var check = db.CalendarInfoes.Find(id);

            if (check == null || check.CStatus != 0)
            {
                return Redirect("/error");
            }

            var startDate = DateTime.ParseExact(check.FDate, "dd/MM/yyyy", null);

            var endDate = DateTime.ParseExact(check.TDate, "dd/MM/yyyy", null);

            List<EditCalendarDay> planTimes = new List<EditCalendarDay>();

            for (DateTime date = startDate; date <= endDate; )
            {
                EditCalendarDay data = new EditCalendarDay()
                {
                    date = date.ToString("dd/MM/yyyy"),
                    code = date.ToString("ddMMyyyy"),
                    dayOfWeek = mapDayOfWeeks[date.DayOfWeek],
                    agency = new List<ShowCalendarAgency>()

                };

                var planCode = date.ToString("ddMMyyyy");

                var listPlan = db.CalendarPlans.Where(p => p.CalendarId == check.Id && p.CDate == planCode).ToList();

                foreach (var item in listPlan)
                {
                    data.agency.Add(new ShowCalendarAgency()
                    {
                        id = item.MAgency.Id,
                        code = item.MAgency.Code,
                        name = item.MAgency.Store,
                        target = item.Targets.Value.ToString("C",Util.Cultures.VietNam)
                    });
                }

                planTimes.Add(data);

                date = date.AddDays(1);
            }

            ViewBag.PlanTime = planTimes;

            return View(check);
        }


        [HttpPost]
        public ActionResult AddCalendarPlan(string date, string id, string agency, int target)
        {
             var checkAgency = db.MAgencies.Where(p => p.Code == agency).FirstOrDefault();
                if (checkAgency == null)
                    return Json(new { id = 0, msg = "Sai đại lý"}, JsonRequestBehavior.AllowGet);
            try
            {
                var check = db.CalendarInfoes.Find(id);

                if (check == null || check.CStatus != 0)
                {
                    throw new Exception("sai thông tin lịch");
                }

                var datePlan = DateTime.ParseExact(date, "ddMMyyyy", null);

                var checkPlan = db.CalendarPlans.Where(p => p.CalendarId == id && p.AgencyId == checkAgency.Id && p.CDate == date).FirstOrDefault();

                if (checkPlan != null)
                    throw new Exception("Đã tạo");

                var plan = new CalendarPlan()
                {
                    Id = Guid.NewGuid().ToString(),
                    AgencyId = checkAgency.Id,
                    CalendarId = check.Id,
                    CDate = date,
                    CDay = datePlan.Day,
                    CMonth = datePlan.Month,
                    CYear = datePlan.Year,
                    DayOfWeek = mapDayOfWeeks[datePlan.DayOfWeek],
                    Targets = target
                };
                db.CalendarPlans.Add(plan);
                db.SaveChanges();
            }
            catch (Exception e)
            {
                return Json(new { id = 0, msg = e.Message }, JsonRequestBehavior.AllowGet);
            }

            return Json(new { id = 1, msg = checkAgency.Store + "<br />target: " + target.ToString("C", Util.Cultures.VietNam), date = date, agencyId = checkAgency.Id }, JsonRequestBehavior.AllowGet);
        }

        [HttpPost]
        public ActionResult RemovePlanAgency(string agency, string date, string calendarId)
        {

            var checkPlan = db.CalendarPlans.Where(p => p.CalendarId == calendarId && p.AgencyId == agency && p.CDate == date).FirstOrDefault();

            if (checkPlan == null)
                return Json(new { id = 0, msg = "Sai thông tin"}, JsonRequestBehavior.AllowGet);

            db.CalendarPlans.Remove(checkPlan);

            db.SaveChanges();

            return Json(new { id = 1 }, JsonRequestBehavior.AllowGet);

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
                Name = "Báo cáo",
                Url = "/calendar/report",
                Active = 0
            });

            if (idxActive < 0 || idxActive >= menues.Count())
                return null;


            menues[idxActive].Active = 1;


            return menues;
        }

        [HttpGet]
        public ActionResult Report()
        {
            AddMenu(2);

            ViewBag.Staff = db.MStaffs.ToList();



            return View();
        }


        [HttpGet]
        public ActionResult ReportCalendarById(string calendarId)
        {

            string pathRoot = Server.MapPath("~/MTemplates/form_checkin_month.xlsx");
            string name = "checkinform" + DateTime.Now.ToString("ddMMyyyyHHmmss") + ".xlsx";
            string pathTo = Server.MapPath("~/Temp/" + name);

            System.IO.File.Copy(pathRoot, pathTo);

            try
            {
                FileInfo newFile = new FileInfo(pathTo);

                using (ExcelPackage package = new ExcelPackage(newFile))
                {
                    ExcelWorksheet worksheet = package.Workbook.Worksheets[1];

                    var data = db.get_calendar_by_id(calendarId).ToList();

                    for (int i = 0; i < data.Count(); i++)
                    {
                        worksheet.Cells[i + 8, 1].Value = i + 1;

                        worksheet.Cells[i + 8, 2].Value = data[i].AgencyCode;
                        worksheet.Cells[i + 8, 3].Value = data[i].Deputy;
                        worksheet.Cells[i + 8, 4].Value = data[i].Province;
                        worksheet.Cells[i + 8, 5].Value = data[i].AgencyAddress;
                        worksheet.Cells[i + 8, 6].Value = data[i].AgencyPhone;
                        worksheet.Cells[i + 8, 7].Value = "Ngày " + data[i].CDay + " tháng" + data[i].CMonth;
                        worksheet.Cells[i + 8, 8].Value = data[i].CInTime;
                        worksheet.Cells[i + 8, 9].Value = data[i].COutTime;
                        worksheet.Cells[i + 8, 10].Value = data[i].StaffName;
                        worksheet.Cells[i + 8, 11].Value = data[i].StaffCheck;
                        worksheet.Cells[i + 8, 12].Value = data[i].Targets;
                        worksheet.Cells[i + 8, 13].Value = data[i].TotalMoney;
                        worksheet.Cells[i + 8, 14].Value = data[i].Notes;
                    }

                    package.Save();
                }

            }
            catch
            {
                return Redirect("/error");
            }


            return File(pathTo, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", string.Format("checkin-" + DateTime.Now.ToString("ddMMyyyyhhmmss") + ".{0}", "xlsx"));
        }


        [HttpGet]
        public ActionResult ReportCheckInMonth(int? month, int? year, string staff = "")
        {
            var staffCheck = db.MStaffs.Find(staff);

            if (staffCheck == null)
                return Redirect("/error");

            string pathRoot = Server.MapPath("~/MTemplates/form_checkin_month.xlsx");
            string name = "checkinform" + DateTime.Now.ToString("ddMMyyyyHHmmss") + ".xlsx";
            string pathTo = Server.MapPath("~/Temp/" + name);

            System.IO.File.Copy(pathRoot, pathTo);

            try
            {
                FileInfo newFile = new FileInfo(pathTo);

                using (ExcelPackage package = new ExcelPackage(newFile))
                {
                    ExcelWorksheet worksheet = package.Workbook.Worksheets[1];

                    worksheet.Cells[2, 1].Value = "Tháng " + month + " năm " + year;

                    worksheet.Cells[4, 3].Value = staffCheck.FullName;

                    worksheet.Cells[5, 3].Value = staffCheck.GroupNumber;

                    var data = db.get_calendar_in_month_by_staff(month, year, staff).ToList();

                    for (int i = 0; i < data.Count(); i++)
                    {
                        worksheet.Cells[i + 8, 1].Value = i + 1;

                        worksheet.Cells[i + 8, 2].Value = data[i].AgencyCode;
                        worksheet.Cells[i + 8, 3].Value = data[i].Deputy;
                        worksheet.Cells[i + 8, 4].Value = data[i].Province;
                        worksheet.Cells[i + 8, 5].Value = data[i].AgencyAddress;
                        worksheet.Cells[i + 8, 6].Value = data[i].AgencyPhone;
                        worksheet.Cells[i + 8, 7].Value = "Ngày " + data[i].CDay + " tháng" + data[i].CMonth;
                        worksheet.Cells[i + 8, 8].Value = data[i].CInTime;
                        worksheet.Cells[i + 8, 9].Value = data[i].COutTime;
                        worksheet.Cells[i + 8, 10].Value = data[i].StaffName;
                        worksheet.Cells[i + 8, 11].Value = data[i].StaffCheck;
                        worksheet.Cells[i + 8, 12].Value = data[i].Targets;
                        worksheet.Cells[i + 8, 13].Value = data[i].TotalMoney;
                        worksheet.Cells[i + 8, 14].Value = data[i].Notes;
                    }

                    package.Save();
                }

            }
            catch
            {
                return Redirect("/error");
            }


            return File(pathTo, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", string.Format("checkin-" + DateTime.Now.ToString("ddMMyyyyhhmmss") + ".{0}", "xlsx"));
        }
    }
}