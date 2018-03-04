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
    
    public partial class MProduct
    {
        public MProduct()
        {
            this.ProductOrders = new HashSet<ProductOrder>();
        }
    
        public string Id { get; set; }
        public string PName { get; set; }
        public string Describes { get; set; }
        public string PImage { get; set; }
        public string PCode { get; set; }
        public Nullable<double> Price { get; set; }
        public string Unit { get; set; }
        public Nullable<int> IsLock { get; set; }
        public Nullable<int> Size { get; set; }
        public string TypeId { get; set; }
    
        public virtual ICollection<ProductOrder> ProductOrders { get; set; }
        public virtual ProductType ProductType { get; set; }
    }
}
