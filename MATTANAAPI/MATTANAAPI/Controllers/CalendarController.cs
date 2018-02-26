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

                var works = db.get_calendar_by_staff_byday(DateTime.Now.Day, DateTime.Now.Month, DateTime.Now.Year, checkStaff.Id).ToList();

                foreach (var item in works)
                {
                    result.works.Add(new CWorkInfo()
                    {
                        workId = item.Id,
                        store = item.Store,
                        phone = item.Phone,
                        lng = item.Lng == null ? 0 : item.Lng,
                        lat = item.Lat == null ? 0 : item.Lat,
                        address = item.AddressDetail,
                        code = item.Code,
                        perform = item.Perform,
                        inplan = item.InPlan,
                        status = item.CInTime != null ? "Ghé thăm lúc: " + new DateTime(item.CInTime.Value.Ticks).ToString("HH:mm") + " - nhân viên: " + item.StaffCheckName : "Chưa ghé thăm"
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
        public ResultInfo CheckIn(string user, string token, string workId)
        {
            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/calendar/checkin",
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
                    throw new Exception("Đã hoàn thành");

                if (checkWork.CInTime != null)
                {
                    if (checkWork.StaffCode != null)
                    {
                        var staffCheck = db.MStaffs.Find(checkWork.StaffCode);
                        if (staffCheck.Id != checkStaff.Id)
                            throw new Exception("Đã được check bởi thành viên trong nhóm bạn");
                    }


                }
                else
                {
                    checkWork.CInTime = DateTime.Now.TimeOfDay;
                    checkWork.StaffCode = checkStaff.Id;

                    db.Entry(checkWork).State = System.Data.Entity.EntityState.Modified;
                    db.SaveChanges();
                }


                result.msg = new DateTime(checkWork.CInTime.Value.Ticks).ToString("HH:mm");


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

                if (checkWork.CInTime == null || checkWork.Perform == 1)
                    throw new Exception("Không thể thực hiện");

                checkWork.Perform = 1;
                checkWork.COutTime = DateTime.Now.TimeOfDay;
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
    }
}
