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

        public string suggestDate { get; set; }

        public List<ProductOrderInfo> products { get; set; } 
    }

    public class ProductOrderInfo
    {
        public string id { get; set; }

        public int quantity { get; set; }
    }

    public class ShowOrderResult : ResultInfo
    {
        public List<ShowOrderInfo> orders { get; set; }
    }


    public class ShowOrderInfo
    {
        public string orderId { get; set; }
        public string code { get; set; }

        public string status { get; set; }

        public string statusCode { get; set; }

        public string nextStatus { get; set; }

        public string nextStatusCode { get; set; }

        public int close { get; set; }

        public string orderPrice { get; set; }

        public string store { get; set; }

        public string agency { get; set; }

        public string address { get; set; }

        public string phone { get; set; }

        public int? productNumber { get; set; }

        public string createTime { get; set; }

        public string timeSuggest { get; set; }

        public string realPrice { get; set; }

        public string staffName { get; set; }

        public string staffCode { get; set; }
    }

    public class ShowProductOrderInfo
    {
        public string name { get; set; }

        public string code { get; set; }

        public string price { get; set; }

        public int quantityBuy { get; set; }

        public string priceTotal { get; set; }

        public int quantityReal { get; set; }

        public string Id { get; set; }

        public string size { get; set; }

    }

    public class UpdateOrderResult : ResultInfo
    {
        public string status { get; set; }

        public string statusCode { get; set; }

        public string nextStatus { get; set; }

        public string nextStatusCode { get; set; }
    }
}