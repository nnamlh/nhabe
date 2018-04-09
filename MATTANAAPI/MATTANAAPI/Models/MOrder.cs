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
    
    public partial class MOrder
    {
        public MOrder()
        {
            this.ProductOrders = new HashSet<ProductOrder>();
        }
    
        public string Id { get; set; }
        public string Code { get; set; }
        public string AgencyId { get; set; }
        public string StaffId { get; set; }
        public Nullable<double> PriceOrder { get; set; }
        public Nullable<double> PriceReal { get; set; }
        public string StatusId { get; set; }
        public Nullable<System.DateTime> CreateTime { get; set; }
        public Nullable<System.DateTime> ModifyTime { get; set; }
        public Nullable<int> CloseOrder { get; set; }
        public string Notes { get; set; }
        public Nullable<System.DateTime> SuggestDate { get; set; }
        public string CDate { get; set; }
    
        public virtual MAgency MAgency { get; set; }
        public virtual MStaff MStaff { get; set; }
        public virtual OrderStatu OrderStatu { get; set; }
        public virtual ICollection<ProductOrder> ProductOrders { get; set; }
    }
}
