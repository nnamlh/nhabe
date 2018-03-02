using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{
    public class IdentityInfo
    {

    }

    public class ProductInfoResult
    {
        public string id { get; set; }

        public string code { get; set; }

        public string name { get; set; }

        public string describes { get; set; }

        public double? price { get; set; }

        public int? size { get; set; }
    }

    public class NoticeInfo
    {
        public string id { get; set; }

        public string title { get; set; }

        public string message { get; set; }

        public string time { get; set; }

        public int read { get; set; }
    }
}