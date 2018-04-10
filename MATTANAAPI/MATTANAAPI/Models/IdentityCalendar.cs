using System;
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

        public double? discount { get; set; }

        public string id { get; set; }

    }

    // checkin
    public class CheckInResult : ResultInfo
    {
        public int? perform { get; set; }

        public string des { get; set; }

        public string workId { get; set; }
    }


    public class ShowCalendarAgency
    {

        public string code { get; set; }

        public string name { get; set; }

        public string target { get; set; }

    }
    public class CalendarWorkDay
    {
        public string date { get; set; }
        public string dayOfWeek { get; set; }
        public List<ShowCalendarAgency> plan { get; set; }

        public List<ShowCalendarAgency> work { get; set; }
    }

    public class CalendarWorkResult : ResultInfo
    {
        public int? week { get; set; }

        public int? year { get; set; }


        public string fDate { get; set; }

        public string tDate { get; set; }

        public List<CalendarWorkDay> works { get; set; }
    }


}