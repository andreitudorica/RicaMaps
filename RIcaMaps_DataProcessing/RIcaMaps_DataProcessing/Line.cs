using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Device.Location;

namespace RIcaMaps_DataProcessing
{
    class Line
    {
        private GeoCoordinate start, end;

        public Line(GeoCoordinate st,GeoCoordinate en)
        {
            start = st;
            end = en;
        }
        public Line(string[] st,string[] en)
        {
            start = new GeoCoordinate(Double.Parse(st[0]), Double.Parse(st[1]));
            end = new GeoCoordinate(Double.Parse(en[0]), Double.Parse(en[1]));
        }
        public double getLength()
        {
            return start.GetDistanceTo(end);
        }
        public String toString()
        {
            return start.ToString() + " " + end.ToString();
        }
    }
}
