using MATTANAAPI.Models;
using MongoDB.Bson;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Util
{
    public class MongoHelper
    {
        IMongoDatabase db = null;

        public MongoHelper()
        {

            string value = ConfigurationManager.AppSettings["MongoServer"];
            if (value == null)
                value = "mongodb://localhost:27017";

            var client = new MongoClient(value);

            db = client.GetDatabase("MLog");
        }

        public void createHistoryAPI(MongoHistoryAPI data)
        {
            var collection = db.GetCollection<MongoHistoryAPI>("APIHistory");
            collection.InsertOneAsync(data);

        }
        // auth history
        public void createAuthHistory(MongoAPIAuthHistory data)
        {
            var collection = db.GetCollection<MongoAPIAuthHistory>("APIAuthHistory");
            collection.InsertOneAsync(data);

        }

        public void createLocationStaff(MongoLocationStaff data)
        {
            var collection = db.GetCollection<MongoLocationStaff>("LocationStaff");
            collection.InsertOneAsync(data);
        }


        public bool checkLoginSession(string user, string token)
        {
            var collection = db.GetCollection<MongoAPIAuthHistory>("APIAuthHistory");
            var builder = Builders<MongoAPIAuthHistory>.Filter;

            var filter = builder.Eq("UserLogin", user) & builder.Eq("Token", token) & builder.Eq("IsExpired", 0);

            var data = collection.Find<MongoAPIAuthHistory>(filter).FirstOrDefault();

            return data == null ? false : true;
        }

        public void checkAndCreateAutHistory(string user, string token, string device, string os, string imei)
        {
            var collection = db.GetCollection<MongoAPIAuthHistory>("APIAuthHistory");
            var builder = Builders<MongoAPIAuthHistory>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<MongoAPIAuthHistory>(builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0)).ToList();

            foreach (var item in data)
            {
                var update = Builders<MongoAPIAuthHistory>.Update.Set("IsExpired", 1);
                var result = collection.UpdateOneAsync(Builders<MongoAPIAuthHistory>.Filter.Eq("Id", item.Id), update);
            }

            var authHistory = new MongoAPIAuthHistory()
            {
                Device = device,
                Imei = imei,
                OS = os,
                Token = token,
                UserLogin = user,
                IsExpired = 0,
                LoginTime = DateTime.Now
            };

            collection.InsertOneAsync(authHistory);
        }

        public void checkAndUpdateFirebase(string user, string firebaseId)
        {
            var collection = db.GetCollection<FirebaseMongo>("FirebaseInfo");
            var builder = Builders<FirebaseMongo>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<FirebaseMongo>(builder.Eq("User", user)).FirstOrDefault();

            if (data == null)
            {
                var firebaseSave = new FirebaseMongo()
                {
                    FirebaseId = firebaseId,
                    User = user,
                    Time = DateTime.Now
                };

                collection.InsertOneAsync(firebaseSave);
            }
            else
            {
                var update = Builders<FirebaseMongo>.Update.Set("FirebaseId", firebaseId);
                var result = collection.UpdateOneAsync(Builders<FirebaseMongo>.Filter.Eq("Id", data.Id), update);
            }
        }

        public List<NoticeMongo> getNotices(string user, DateTime fDate, DateTime tDate)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");
            var builder = Builders<NoticeMongo>.Filter;
            tDate.AddDays(1);
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<NoticeMongo>(builder.Eq("User", user) & builder.Gte("Time", fDate) & builder.Lt("Time", tDate)).ToList();

            return data;
        }

        public int countNoticesNotRead(string user)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");
            var builder = Builders<NoticeMongo>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<NoticeMongo>(builder.Eq("User", user) & builder.Eq("Read", 0)).ToList();

            return data.Count();
        }


        public void updateNotice(string id)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");
            var builder = Builders<NoticeMongo>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<NoticeMongo>(builder.Eq("Id", new ObjectId(id))).FirstOrDefault();

            if (data != null)
            {
                var update = Builders<NoticeMongo>.Update.Set("Read", 1);
                var result = collection.UpdateOneAsync(Builders<NoticeMongo>.Filter.Eq("Id", new ObjectId(id)), update);
            }
        }

        public void updateStateAuthToken(string user)
        {
            var collection = db.GetCollection<MongoAPIAuthHistory>("APIAuthHistory");
            var builder = Builders<MongoAPIAuthHistory>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<MongoAPIAuthHistory>(builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0)).ToList();

            foreach (var item in data)
            {
                var update = Builders<MongoAPIAuthHistory>.Update.Set("IsExpired", 1);
                var result = collection.UpdateOneAsync(Builders<MongoAPIAuthHistory>.Filter.Eq("Id", item.Id), update);
            }
        }

        public void updateLocationForStaff(string user,string name, string code, double lat, double lng)
        {
            var collection = db.GetCollection<LocationStaffSave>("LocationStaffSave");
            var builder = Builders<LocationStaffSave>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<LocationStaffSave>(builder.Eq("User", user)).FirstOrDefault();
            if (data != null)
            {
                var update = Builders<LocationStaffSave>.Update.Set("Lat", lat).Set("Lng", lng).Set("Time", DateTime.Now);
                var result = collection.UpdateOneAsync(Builders<LocationStaffSave>.Filter.Eq("Id", data.Id), update);
            }
            else
            {

                var newLocation = new LocationStaffSave()
                {
                    User = user,
                    Lat = lat,
                    Lng = lng,
                    Time = DateTime.Now,
                    Name = name,
                    Code = code
                };
                collection.InsertOneAsync(newLocation);
            }
        }


        // logout
        public void saveLogout(string user, string token)
        {
            var collection = db.GetCollection<MongoLogoutHistory>("LogoutHistory");

            var data = new MongoLogoutHistory()
            {
                LogoutTime = DateTime.Now,
                Token = token,
                UserLogin = user
            };

            collection.InsertOneAsync(data);
        }

        public string findFirebaseId(string user)
        {
            var collection = db.GetCollection<FirebaseMongo>("FirebaseInfo");
            var builder = Builders<FirebaseMongo>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<FirebaseMongo>(builder.Eq("User", user)).FirstOrDefault();

            if (data != null)
                return data.FirebaseId;

            return "";

        }

        public void saveNoticeHistory(string user, string message, string title)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");

            var notice = new NoticeMongo()
            {
                Message = message,
                User = user,
                Time = DateTime.Now,
                Type = "notice",
                Read = 0,
                Title = title
            };

            collection.InsertOneAsync(notice);
        }

    }
}