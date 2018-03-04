using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MattanaSite.Models
{
    public class IdentityCommon
    {
    }

    public class SubMenuInfo
    {
        public string Name { get; set; }

        public string Url { get; set; }

        public int Active { get; set; }
    }


    public class LocationInfo
    {
        public double lat { get; set; }

        public double lng { get; set; }

    }

    public class LocationStaffInfo
    {
        public string User { get; set; }

        public double Lat { get; set; }

        public double Lng { get; set; }

        public string Name { get; set; }

        public string Code { get; set; }


        public string Time { get; set; }
    }
}