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

        public bool GetUserLocation(List<string> users)
        {
            var collection = db.GetCollection<MongoLocationStaff>("LogoutHistory");
            var builder = Builders<MongoLocationStaff>.Filter;

            var filter = builder.In("Code", users);

            var data = collection.Find<MongoLocationStaff>(filter).ToList().Max(p=> p;

            return data == null ? false : true;
        }
    }
}