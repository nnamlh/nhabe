using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{
    public class IdentityOrder
    {
    }

    public class CreateOrderRequest : RequestInfo
    {
        public string agencyId { get; set; }

        public List<ProductOrderInfo> products { get; set; } 
    }

    public class ProductOrderInfo
    {
        public string id { get; set; }

        public int quantity { get; set; }
    }
}