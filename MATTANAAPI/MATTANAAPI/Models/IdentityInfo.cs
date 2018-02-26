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
    }
}