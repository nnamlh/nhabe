using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MATTANAAPI.Models;
using MATTANAAPI.Util;
using System.Web.Script.Serialization;

namespace MATTANAAPI.Controllers
{
    public class OrderController : BaseController
    {

        [HttpPost]
        public ResultInfo CreateOrder()
        {

            var log = new MongoHistoryAPI()
        {
            APIUrl = "/api/calendar/createorder",
            CreateTime = DateTime.Now,
            Sucess = 1
        };

            var result = new ResultInfo()
            {
                id = "1",
                msg = "success"
            };

            try
            {
                var requestContent = Request.Content.ReadAsStringAsync().Result;
                var jsonserializer = new JavaScriptSerializer();
                var paser = jsonserializer.Deserialize<CreateOrderRequest>(requestContent);
                log.Content = new JavaScriptSerializer().Serialize(paser);

                if (!mongoHelper.checkLoginSession(paser.user, paser.token))
                    throw new Exception("Wrong token and user login!");


                var checkStaff = db.MStaffs.Where(p => p.MUser == paser.user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");

                var checkAgency = db.MAgencies.Where(p=> p.Code == paser.agencyId).FirstOrDefault();

                if (checkAgency == null)
                    throw new Exception("Sai thông tin");

                var newCode = GetCode();

                var order = new MOrder()
                {
                    Id = Guid.NewGuid().ToString(),
                    CreateTime = DateTime.Now,
                    AgencyId = checkAgency.Id,
                    CloseOrder = 0,
                    ModifyTime = DateTime.Now,
                    PriceReal = 0,
                    StaffId = checkStaff.Id,
                    StatusId = "create",
                    Code = newCode
                };

                db.MOrders.Add(order);
                db.SaveChanges();

                double? priceTotal = 0;

                foreach (var item in paser.products)
                {
                    var checkProduct = db.MProducts.Find(item.id);
                    if (checkProduct != null)
                    {
                        var price = checkProduct.Price * item.quantity;

                        priceTotal += price;

                        var productOder = new ProductOrder()
                        {
                            OrderId = order.Id,
                            Price = checkProduct.Price,
                            ProductId = checkProduct.Id,
                            QuantityBuy = item.quantity,
                            QuantityReal = 0
                        };
                        db.ProductOrders.Add(productOder);
                        db.SaveChanges();
                    }
                }


                order.PriceOrder = priceTotal;
                db.Entry(order).State = System.Data.Entity.EntityState.Modified;
                db.SaveChanges();

            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;


        }

        private string GetCode()
        {
            var charId = db.OrderNumbers.Max(p => p.CharId);

            if (charId == null)
            {
                charId = 65;
            }

            var number = db.OrderNumbers.Where(p => p.CharId == charId).Max(p => p.Number);

            if (number == null)
                number = 10000;
            else
            {
                if (number == 99999)
                {
                    charId++;
                    number = 10000;
                }
                else
                {
                    number++;
                }
            }

            char character = (char)charId;
            string code = character.ToString() + number;

            var save = new OrderNumber()
            {
                Id = code,
                CharId = charId,
                Number = number
            };
            db.OrderNumbers.Add(save);
            db.SaveChanges();

            return code;

        }


    }
}
