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

    public class ShowCalendarAgency {


        public string id { get; set; }
        public string code { get; set; }

        public string name { get; set; }

        public string target { get; set; }

    }

    public class EditCalendarDay
    {
        public string code { get; set; }
        public string date { get; set; }
        public string dayOfWeek { get; set; }
        public List<ShowCalendarAgency> agency { get; set; }
    }

    public class ShowCalendarDay
    {
        public string code { get; set; }
        public string date { get; set; }
        public string dayOfWeek { get; set; }
        public List<ShowCalendarAgency> plan { get; set; }

        public List<ShowCalendarAgency> work { get; set; }
    }
}