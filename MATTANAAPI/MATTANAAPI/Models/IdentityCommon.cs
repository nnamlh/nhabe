using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{

    public class IdentityCommon
    {
        public string code { get; set; }
        public string name { get; set; }
    }


    public class ResultInfo
    {
        public string id { get; set; }
        public string msg { get; set; }
    }

    public class RequestInfo
    {
        public string user { get; set; }
        public string token { get; set; }

    }


    public class MainInfoRequest : RequestInfo
    {
        public string firebaseId { get; set; }
    }

    public class MainInfoResult : ResultInfo
    {
        public string role { get; set; }

        public int notices { get; set; }
    }

}