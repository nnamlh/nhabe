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

        public void saveNoticeHistory(string user, string message)
        {
            var collection = db.GetCollection<NoticeMongo>("NoticeHistory");

            var notice = new NoticeMongo()
            {
                Message = message,
                User = user,
                Time = DateTime.Now,
                Type = "notice",
                Read = 0
            };

            collection.InsertOneAsync(notice);
        }
    }
}