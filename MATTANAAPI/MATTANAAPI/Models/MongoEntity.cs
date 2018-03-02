using MongoDB.Bson;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{
    public class MongoHistoryAPI
    {
        public ObjectId Id { get; set; }
        public string APIUrl { get; set; }
        public string Content { get; set; }
        public DateTime CreateTime { get; set; }
        public int Sucess { get; set; }
        public string Error { get; set; }
        public string ReturnInfo { get; set; }
    }

    public class MongoAPIAuthHistory
    {
        public ObjectId Id { get; set; }
        public string UserLogin { get; set; }
        public Nullable<System.DateTime> LoginTime { get; set; }
        public Nullable<int> IsExpired { get; set; }
        public string Token { get; set; }

        public string Device { get; set; }

        public string Imei { get; set; }

        public string OS { get; set; }

    }

    public class MongoLogoutHistory
    {
        public ObjectId Id { get; set; }
        public string UserLogin { get; set; }
        public Nullable<System.DateTime> LogoutTime { get; set; }
        public string Token { get; set; }
    }

    public class MongoLocationStaff
    {
        public ObjectId Id { get; set; }

        public string User { get; set; }

        public double Lat { get; set; }

        public double Lng { get; set; }

        public Nullable<System.DateTime> Time { get; set; }

        public string Name { get; set; }

        public string Code { get; set; }

        public string Device { get; set; }

        public string OS { get; set; }
    }

    public class FirebaseMongo
    {
        public ObjectId Id { get; set; }

        public string User { get; set; }

        public string FirebaseId { get; set; }

        public Nullable<System.DateTime> Time { get; set; }
    }

    public class NoticeMongo
    {
        public ObjectId Id { get; set; }

        public string User { get; set; }

        public string Message { get; set; }

        public string Title { get; set; }

        public string Type { get; set; }

        public int Read { get; set; }

        public Nullable<System.DateTime> Time { get; set; }
    }

    public class LocationStaffSave
    {
        public ObjectId Id { get; set; }

        public string User { get; set; }

        public double Lat { get; set; }

        public double Lng { get; set; }

        public Nullable<System.DateTime> Time { get; set; }
    }
}