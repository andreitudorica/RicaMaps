﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Device.Location;

namespace RIcaMaps_DataProcessing
{
    class Line
    {
        public double topSpeed = 50 * 1000 / 3600;
        public Node start, end;
        public List<int> delays;
        public Line(Node st, Node en)
        {
            start = st;
            end = en;
            delays = new List<int>();
        }
        public double getLength()
        {
            return start.GetDistanceToNode(end);
        }
        public double getCost(double dist, double t)
        {
            return dist / (topSpeed * getDelayForHour(t)/ 100);//segment/viteza (viteza=procent din 50km/h transformat in m/s)
        }
        public int getDelayForHour(double t)
        {
            return delays[(int)t / 3600];
        }
        public void setDelays(List<int> d)
        {
            delays = d;
        }
        public String toString()
        {
            return "[[" + start.toString() + "],[" + end.toString() + "]]";
        }
        public double computeTime(double t)
        {
            double cost = getCost(getLength(), t % 1440);
            if (t / 3600 != ((t + cost) / 3600))
            {
                double cost1 = t - t % 3600;
                double dist1 = t * topSpeed * getDelayForHour(t) / 100;
                double cost2 = getCost(getLength() - dist1, t);
                return cost1 + cost2;
            }
            return cost;
        }
    }
}
