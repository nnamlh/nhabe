using MATTANAAPI.Models;
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

    }
}