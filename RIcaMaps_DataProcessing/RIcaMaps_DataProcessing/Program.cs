using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Device.Location;

namespace RIcaMaps_DataProcessing
{
    class details
    {
        public double dist;
        public Node previous;
        public details(int i, Node n)
        {
            dist = i;
            previous = n;
        }
    }
    class Program
    {
        public StreamWriter outFile = new StreamWriter(@"..\..\Routes.txt");
        private static List<Street> streets = new List<Street>();
        private static List<Node> nodes = new List<Node>();
        private static Node NodeOrNull(double a, double b)
        {
            return nodes.DefaultIfEmpty(null).FirstOrDefault(n => n!=null && n.location.Latitude == a && n.location.Longitude == b);
        }
        private static Street StreetOrNull(String s)
        {
            return streets.DefaultIfEmpty(null).FirstOrDefault(str => str!=null && str.name.Equals(s));
        }
        private static void ReadData()
        {
            StreamReader reader = File.OpenText("../../../polylines.txt");
            String line;
            while ((line = reader.ReadLine()) != null)
            {
                bool existingStreet = false;
                Street street = StreetOrNull(line);
                if (street == null)
                    street=new Street(line);
                else
                    existingStreet = true;
                int n = Int32.Parse(reader.ReadLine());
                string[] coords = reader.ReadLine().Split(',');
                Node node = NodeOrNull(Double.Parse(coords[0]), Double.Parse(coords[1]));
                if (node == null)
                {
                    node = new Node(new GeoCoordinate(Double.Parse(coords[0]), Double.Parse(coords[1])));
                    nodes.Add(node);
                }

                for (int i = 1; i < n; i++)
                {
                    string[] coords1 = reader.ReadLine().Split(',');
                    Node node1 = NodeOrNull(Double.Parse(coords1[0]), Double.Parse(coords1[1]));
                    if (node1 == null)
                    {
                        node1 = new Node(new GeoCoordinate(Double.Parse(coords1[0]), Double.Parse(coords1[1])));
                        nodes.Add(node1);
                    }
                    street.addLine(new Line(node, node1));
                    node.containingLines.Add(street.polyLine[street.polyLine.Count - 1]);
                    //node1.containingLines.Add(street.polyLine[street.polyLine.Count - 1]);
                    node.neighbours.Add(node1);
                    node = node1;
                }
                if (existingStreet == false)
                    streets.Add(street);
            }
            reader = File.OpenText("../../../delays.txt");
            while ((line = reader.ReadLine()) != null)
            {
                Street street = streets.Find(o => o.name == line);
                for (int i = 1; i <= 24; i++)
                    street.delays.Add(Int32.Parse(reader.ReadLine()));
                street.setDelaysOnLines();
            }
        }

        private static void FindWays(int t, Node st)
        {
            Dictionary<Node, details> D = new Dictionary<Node, details>();
            bool ok;
            foreach (Node n in nodes)
                D[n] = new details(1000000, null);
            D[st].dist = 0 ;
            foreach (Line l in st.containingLines)
            {
                D[l.end].dist = l.computeTime(t);
                D[l.end].previous = st;
            }
            do
            {
                ok = true;
                foreach (Node n in nodes)
                    foreach (Line l in n.containingLines)
                    {
                        if (D[l.end].dist > D[n].dist + l.computeTime(t + D[n].dist))
                        {
                            D[l.end].dist = D[n].dist + l.computeTime(t + D[n].dist);
                            ok = false;
                        }
                    }
            } while (!ok);
            using (StreamWriter outFile = new StreamWriter(@"..\..\Routes.txt"))
            {
                foreach (Node n in nodes)
                    if (n != st)
                    {
                        List<Node> result = new List<Node>();
                        Node current = n;
                        while (D[current].previous != null)
                        {
                            result.Add(current);
                            current = D[current].previous;
                        }
                        String res = String.Empty;
                        res = res + st.toString() + " " + n.toString() + " " + t / 3600 + ":" + t % 3600 / 60 + " ";
                        foreach (Node nod in result)
                            res += nod.toString();
                        outFile.WriteLine(res);
                    }
            }
        }
        static void Main(string[] args)
        {
            ReadData();
            foreach (Street street in streets)
                Console.WriteLine(street.toString());

            foreach (Node n in nodes)
                for (int i = 0; i < 1440; i++)
                    FindWays(i * 60, n);
        }
    }
}
