//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MATTANAAPI.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class CalendarInfo
    {
        public CalendarInfo()
        {
            this.CalendarPlans = new HashSet<CalendarPlan>();
        }
    
        public string Id { get; set; }
        public string FDate { get; set; }
        public string TDate { get; set; }
        public string StaffId { get; set; }
        public Nullable<int> WeekOfYear { get; set; }
        public Nullable<int> CStatus { get; set; }
        public Nullable<int> CMonth { get; set; }
        public Nullable<int> CYear { get; set; }
        public Nullable<System.DateTime> CreateTine { get; set; }
        public string Title { get; set; }
    
        public virtual MStaff MStaff { get; set; }
        public virtual ICollection<CalendarPlan> CalendarPlans { get; set; }
    }
}
