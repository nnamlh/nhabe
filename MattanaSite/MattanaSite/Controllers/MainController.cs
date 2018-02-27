using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MattanaSite.Models;
using System.Globalization;

namespace MattanaSite.Controllers
{

    [Authorize(Roles = "Admin")]
    public abstract class MainController : Controller
    {

        public abstract List<SubMenuInfo> Menu(int idxActive);
        protected Dictionary<DayOfWeek, string> mapDayOfWeeks;

        public MainController()
        {
            mapDayOfWeeks = new Dictionary<DayOfWeek, string>();

            mapDayOfWeeks[DayOfWeek.Monday] = "T2";
            mapDayOfWeeks[DayOfWeek.Tuesday] = "T3";
            mapDayOfWeeks[DayOfWeek.Wednesday] = "T4";
            mapDayOfWeeks[DayOfWeek.Thursday] = "T5";
            mapDayOfWeeks[DayOfWeek.Friday] = "T6";
            mapDayOfWeeks[DayOfWeek.Saturday] = "T7";
            mapDayOfWeeks[DayOfWeek.Sunday] = "CN";

        }
        protected void AddMenu(int idxActive)
        {
            ViewBag.SubMenu = Menu(idxActive);
        }

        protected string GetDayOfWeek(int day, int month, int year)
        {
            DateTime dt = new DateTime(year, month, day);
            return mapDayOfWeeks[dt.DayOfWeek];
        }


        protected List<DateTime> DateRange(DateTime fromDate, DateTime toDate)
        {
            return Enumerable.Range(0, toDate.Subtract(fromDate).Days + 1)
                             .Select(d => fromDate.AddDays(d)).ToList();
        }

        protected int GetDaysInMonth(int year, int month)
        {
            int days = DateTime.DaysInMonth(year, month);

            return days;
        }

        protected static int GetIso8601WeekOfYear(DateTime time)
        {
            // Seriously cheat.  If its Monday, Tuesday or Wednesday, then it'll 
            // be the same week# as whatever Thursday, Friday or Saturday are,
            // and we always get those right
            DayOfWeek day = CultureInfo.InvariantCulture.Calendar.GetDayOfWeek(time);
            if (day >= DayOfWeek.Monday && day <= DayOfWeek.Wednesday)
            {
                time = time.AddDays(3);
            }

            // Return the week of our adjusted day
            return CultureInfo.InvariantCulture.Calendar.GetWeekOfYear(time, CalendarWeekRule.FirstFourDayWeek, DayOfWeek.Monday);
        }

        
	}
}