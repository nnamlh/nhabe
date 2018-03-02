using MattanaSite.Models;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;

namespace MattanaSite.Util
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


        public List<MongoLocationStaff> ShowLocationStaff(string user, DateTime date)
        {
            var collection = db.GetCollection<MongoLocationStaff>("LocationStaff");
            var builder = Builders<MongoLocationStaff>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var datF = date.AddDays(1);
            var data = collection.Find<MongoLocationStaff>(builder.Eq("User", user) & builder.Gt("Time", date) & builder.Lt("Time", datF)).ToList();


            return data;

        }

        public void ShowLocationStaffInDay(string user)
        {
            var collection = db.GetCollection<MongoLocationStaff>("LocationStaff");
            var builder = Builders<MongoLocationStaff>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<MongoLocationStaff>(builder.Eq("User", user) & builder.Eq("Time", DateTime.Now)).ToList();

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

        public LocationStaffSave findNewLocationStaff(string user)
        {
            var collection = db.GetCollection<LocationStaffSave>("LocationStaffSave");
            var builder = Builders<LocationStaffSave>.Filter;
            //  var filter = builder.Eq("UserLogin", user) & builder.Eq("IsExpired", 0);
            var data = collection.Find<LocationStaffSave>(builder.Eq("User", user)).FirstOrDefault();
            return data;
        }

        public void saveNoticeHistory(string user,string title, string message)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");

            var notice = new NoticeMongo()
            {
                Message = message,
                User = user,
                Title = title,
                Time = DateTime.Now,
                Type = "notice",
                Read = 0
            };

            collection.InsertOneAsync(notice);
        }
    }
}