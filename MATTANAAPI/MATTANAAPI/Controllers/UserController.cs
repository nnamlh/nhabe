using MATTANAAPI.Models;
using MATTANAAPI.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Script.Serialization;

namespace MATTANAAPI.Controllers
{
    public class UserController : BaseController
    {

        #region login
        /// <summary>
        /// danh cho login logout
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<LoginResult> Login()
        {
            // login
            // /api/rest/login
            // method: get
            HttpRequestHeaders headers = Request.Headers;

            LoginResult check = await Auth(headers);

            var history = new MongoHistoryAPI()
            {
                CreateTime = DateTime.Now,
                APIUrl = "/api/user/login",
                ReturnInfo = new JavaScriptSerializer().Serialize(check)
            };

            if (check.id == "0")
            {
                history.Sucess = 0;
            }
            else history.Sucess = 1;

            mongoHelper.createHistoryAPI(history);

            return check;
        }

        #endregion

        #region Auth
        private async Task<LoginResult> Auth(HttpRequestHeaders headers)
        {

            if (!headers.Contains("Authorization"))
            {
                return new LoginResult() { id = "0", msg = "Nead authorization info" };
            }

            string token;

            try
            {
                string base64Auth = headers.GetValues("Authorization").First().Replace("Basic", "").Trim();
                token = XString.FromBase64(base64Auth);
            }
            catch
            {
                return new LoginResult { id = "0", msg = "Wrong authorization info" };
            }

            var arrtok = token.Split(':');

            if (arrtok.Length != 2)
                return new LoginResult { id = "0", msg = "Wrong authorization format" };

            string UserName = arrtok[0];
            string PassWord = arrtok[1];

            var user = await UserManager.FindAsync(UserName, PassWord);

            if (user == null)
            {
                return new LoginResult { id = "0", msg = "User or Password not correct", user = UserName };
            }

            var check = db.MStaffs.Where(p => p.MUser == UserName).FirstOrDefault();

            if (check == null)
                return new LoginResult { id = "0", msg = "Không tìm thấy thông tin nhân viên", user = UserName };


            if (check.IsLock == 1)
                return new LoginResult { id = "0", msg = "Tài khoản bị khóa.", user = UserName };


            var authToke = Guid.NewGuid().ToString();

            mongoHelper.checkAndCreateAutHistory(user.UserName, authToke, "", "", "");

            string role = "User";

            if (isAdmin(UserName))
                role = "Admin";

            return new LoginResult { id = "1", msg = "login success", token = authToke, user = UserName, code = check.Id, name = check.FullName, role = role };
        }
        #endregion

        #region LoginSession
        /// <summary>
        /// --------------------------------
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public ResultInfo LoginSession(string user, string token)
        {
            var history = new MongoHistoryAPI()
            {
                CreateTime = DateTime.Now,
                APIUrl = "/api/user/loginsession",
                Content = user + "|" + token,
                Sucess = 1
            };

            var result = new ResultInfo()
            {
                id = "1"
            };

            if (!mongoHelper.checkLoginSession(user, token))
            {
                result.id = "0";
                result.msg = "Tài khoản bạn đã đăng nhập ở thiết bị khác.";
                history.Sucess = 0;
            }

            history.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(history);

            return result;

        }
        #endregion


        #region location

        [HttpGet]
        public ResultInfo SaveLocation(double lat, double lng, string user, string name, string code)
        {

            MongoLocationStaff data = new MongoLocationStaff()
            {
                Lat = lat,
                Lng = lng,
                Name = name,
                Code = code,
                Time = DateTime.Now,
                User = user
            };

            mongoHelper.createLocationStaff(data);

            mongoHelper.updateLocationForStaff(user, name, code, lat, lng);

            return new ResultInfo()
            {
                id = "1",
                msg = "ok"
            };
        }


        #endregion


        #region main load

        [HttpPost]
        public MainInfoResult MainLoad()
        {

            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/user/mainload",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

            var result = new MainInfoResult()
           {
               id = "1",
               msg = "success"
           };

            try
            {
                var requestContent = Request.Content.ReadAsStringAsync().Result;
                var jsonserializer = new JavaScriptSerializer();
                var paser = jsonserializer.Deserialize<MainInfoRequest>(requestContent);
                log.Content = new JavaScriptSerializer().Serialize(paser);

                if (!mongoHelper.checkLoginSession(paser.user, paser.token))
                    throw new Exception("Tài khoản bạn đã bị đăng nhập trên thiết bị khác");

                mongoHelper.checkAndUpdateFirebase(paser.user, paser.firebaseId);

                //
                if (isAdmin(paser.user))
                    result.role = "Admin";
                else
                    result.role = "";

                result.notices = mongoHelper.countNoticesNotRead(paser.user);

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

        #endregion

        [HttpGet]
        public ResultInfo LogOut(string user, string token)
        {
            mongoHelper.checkAndUpdateFirebase(user, "");

            mongoHelper.saveLogout(user, token);

            return new ResultInfo()
            {
                id = "1",
                msg = "success"
            };
        }


    }
}
