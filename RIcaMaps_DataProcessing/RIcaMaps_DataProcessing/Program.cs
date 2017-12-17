using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Device.Location;

namespace RIcaMaps_DataProcessing
{
    class Program
    {
        static void Main(string[] args)
        {
            StreamReader reader = File.OpenText("../../../polylines.txt");
            List<Street> streets = new List<Street>();
            String line;
            while ((line = reader.ReadLine()) != null)
            {
                Street street = new Street(line);
                int n = Int32.Parse(reader.ReadLine());
                string[] coords = reader.ReadLine().Split(',');
                for (int i = 1; i < n; i++)
                {
                    string[] coords1 = reader.ReadLine().Split(',');
                    street.addLine(new Line(coords, coords1));
                    coords = coords1;
                }
                streets.Add(street);
            }
            foreach (Street s in streets)
                Console.WriteLine(s.toString());
        }
    }
}
