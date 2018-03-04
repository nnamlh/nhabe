//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MattanaSite.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class MAgency
    {
        public MAgency()
        {
            this.CalendarWorks = new HashSet<CalendarWork>();
            this.MStaffs = new HashSet<MStaff>();
            this.MOrders = new HashSet<MOrder>();
        }
    
        public string Id { get; set; }
        public string Code { get; set; }
        public string Store { get; set; }
        public string Deputy { get; set; }
        public string IdentityCard { get; set; }
        public string Phone { get; set; }
        public string AddressDetail { get; set; }
        public string Province { get; set; }
        public string AreaId { get; set; }
        public Nullable<int> IsLock { get; set; }
        public Nullable<double> Lat { get; set; }
        public Nullable<double> Lng { get; set; }
        public Nullable<double> Discount { get; set; }
    
        public virtual AreaInfo AreaInfo { get; set; }
        public virtual ICollection<CalendarWork> CalendarWorks { get; set; }
        public virtual ICollection<MStaff> MStaffs { get; set; }
        public virtual ICollection<MOrder> MOrders { get; set; }
    }
}
