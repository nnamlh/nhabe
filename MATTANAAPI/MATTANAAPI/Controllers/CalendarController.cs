using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MATTANAAPI.Models;
using System.Web.Script.Serialization;
using MATTANAAPI.Util;

namespace MATTANAAPI.Controllers
{
    public class CalendarController : BaseController
    {

        [HttpGet]
        public CWorkResult ShowWork(string user)
        {

            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/calendar/showwork",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

            var result = new CWorkResult()
            {
                id = "1",
                msg = "success",
                works = new List<CWorkInfo>()
            };

            try
            {
                var checkStaff = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");

                var works = db.get_calendar_by_staff_byday(GetIso8601WeekOfYear(DateTime.Now), DateTime.Now.Year, checkStaff.Id).ToList();

                foreach (var item in works)
                {
                    result.works.Add(new CWorkInfo()
                    {
                        store = item.Store,
                        phone = item.Phone,
                        lng = item.Lng == null ? 0 : item.Lng,
                        lat = item.Lat == null ? 0 : item.Lat,
                        address = item.AddressDetail,
                        code = item.Code,
                        id = item.Id,
                        discount = item.Discount == null? 0: item.Discount
                    });
                }

            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;

        }


        // check in
        [HttpGet]
        public CheckInResult CheckIn(string user, string token, string agencyId)
        {
            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/calendar/checkin",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

            var result = new CheckInResult()
            {
                id = "1",
                msg = "success"
            };
            try
            {
                if (!mongoHelper.checkLoginSession(user, token))
                    throw new Exception(MRes.MSG_WRONG_TOKEN);

                var checkStaff = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");


                var checkAgency = db.MAgencies.Find(agencyId);

                if (checkAgency == null)
                    throw new Exception("Sai đại lý");

                var dateCode = DateTime.Now.ToString("ddMMyyyy");

                var checkWork = db.CalendarWorks.Where(p => p.AgencyId == agencyId && p.StaffId == checkStaff.Id && p.CDate == dateCode).FirstOrDefault();

                if (checkWork == null)
                {
                    var work = new CalendarWork()
                    {
                        AgencyId = agencyId,
                        CDate = dateCode,
                        CDay = DateTime.Now.Day,
                        ChangeTime = DateTime.Now,
                        CMonth = DateTime.Now.Month,
                        CountWork = 1,
                        CYear = DateTime.Now.Year,
                        FistTime = DateTime.Now,
                        Id = Guid.NewGuid().ToString(),
                        Perform = 0,
                        StaffId = checkStaff.Id,
                        DayOfWeek = mapDayOfWeeks[DateTime.Now.DayOfWeek]
                    };

                    db.CalendarWorks.Add(work);
                    db.SaveChanges();

                    result.perform = 0;
                    result.workId = work.Id;
                    result.des = "Ghé thăm lúc " + DateTime.Now.ToString("HH:mm") + " ngày " + DateTime.Now.ToString("dd/MM/yyyy");
                 }
                else
                {

                    result.perform = checkWork.Perform;
                    result.workId = checkWork.Id;

                    result.des = "Ghé thăm giần nhất lúc " + checkWork.ChangeTime.Value.ToString("HH:mm") + " ngày " + checkWork.ChangeTime.Value.ToString("dd/MM/yyyy");

                    checkWork.ChangeTime = DateTime.Now;

                    db.Entry(checkWork).State = System.Data.Entity.EntityState.Modified;
                    db.SaveChanges();
                }

            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;
        }


        [HttpGet]
        public ResultInfo CheckOut(string user, string token, string workId, string notes)
        {
            var log = new MongoHistoryAPI()
           {
               APIUrl = "/api/calendar/checkout",
               CreateTime = DateTime.Now,
               Sucess = 1
           };

            var result = new ResultInfo()
            {
                id = "1",
                msg = "success"
            };
            try
            {
                if (!mongoHelper.checkLoginSession(user, token))
                    throw new Exception(MRes.MSG_WRONG_TOKEN);

                var checkStaff = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");


                var checkWork = db.CalendarWorks.Find(workId);

                if (checkWork == null)
                    throw new Exception("Sai thông tin");

                if (checkWork.Perform == 1)
                    throw new Exception("Đã check out");


                checkWork.EndTime = DateTime.Now;
                checkWork.Perform = 1;
                checkWork.Notes = notes;
                db.Entry(checkWork).State = System.Data.Entity.EntityState.Modified;

                db.SaveChanges();
 
            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;
        }

        //
        [HttpGet]
        public CalendarWorkResult CalendarWork(string user, int? week, int? year)
        {

            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/calendar/calendarwork",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

            var result = new CalendarWorkResult()
            {
                id = "1",
                msg = "success",
                works = new List<CalendarWorkDay>()
            };

            if (week == null || week == 0)
                week = GetIso8601WeekOfYear(DateTime.Now);

            if (year == null || year == 0)
                year = DateTime.Now.Year;

            result.week = week;
            result.year = year;
            var firstWeekCreate = FirstDateOfWeekISO8601((int)year, (int)week);
            result.fDate = firstWeekCreate.ToString("dd/MM/yyyy");
            result.tDate = firstWeekCreate.AddDays(5).ToString("dd/MM/yyyy");
            try
            {
                var checkStaff = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");

             

                var findCal = db.CalendarInfoes.Where(p => p.WeekOfYear == week && p.CYear == year && p.StaffId == checkStaff.Id).FirstOrDefault();

                if (findCal == null)
                {
                    throw new Exception("Chưa có lịch");
                }


                var startDate = DateTime.ParseExact(findCal.FDate, "dd/MM/yyyy", null);

                var endDate = DateTime.ParseExact(findCal.TDate, "dd/MM/yyyy", null);

                for (DateTime date = startDate; date <= endDate; )
                {
                    CalendarWorkDay data = new CalendarWorkDay()
                    {
                        date = date.ToString("dd/MM/yyyy"),
                        dayOfWeek = mapDayOfWeeks[date.DayOfWeek],
                        plan = new List<ShowCalendarAgency>(),
                        work = new List<ShowCalendarAgency>()

                    };

                    var planCode = date.ToString("ddMMyyyy");

                    var listPlan = db.CalendarPlans.Where(p => p.CalendarId == findCal.Id && p.CDate == planCode).ToList();

                    foreach (var item in listPlan)
                    {
                        data.plan.Add(new ShowCalendarAgency()
                        {
                            code = item.MAgency.Code,
                            name = item.MAgency.Store,
                            target = item.Targets.Value.ToString("C", Util.Cultures.VietNam)
                        });
                    }

                    var listWork = db.CalendarWorks.Where(p => p.StaffId == findCal.StaffId && p.CDate == planCode && p.Perform == 1).ToList();

                    foreach (var item in listWork)
                    {
                        data.work.Add(new ShowCalendarAgency()
                        {
                            code = item.MAgency.Code,
                            name = item.MAgency.Store
                        });
                    }


                    result.works.Add(data);

                    date = date.AddDays(1);
                }
              
            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;

        }


    }
}
