﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{
    public class IdentityCalendar
    {
    }

    public class CWorkResult : ResultInfo
    {
        public List<CWorkInfo> works { get; set; }
    }

    public class CWorkInfo
    {
        public string store { get; set; }

        public string phone { get; set; }

        public string address { get; set; }

        public double? lat { get; set; }

        public double? lng { get; set; }

        public string code { get; set; }

        public string id { get; set; }

    }

    // checkin
 
}