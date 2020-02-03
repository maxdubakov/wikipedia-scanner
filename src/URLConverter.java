import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class URLConverter {

    // with maps and queues
    private HashMap<String, LinkedList<String>> data;
    private Queue<String> links;


    URLConverter() {
        data = new HashMap<>();
        links = new LinkedList<>();
    }

    void scanWebsiteForLinks(String url, int maximumQuantity) {

        try {

            links.add(url);

            MAIN_LOOP:
            while (!links.isEmpty()) {
                LinkedList<String> sites = new LinkedList<>();
                if (maximumQuantity <= 0) return;

                // checking if everything is OK and we don't have any doubles
                for (int i = 0; i < data.keySet().size(); i++) {
                    if (links.element().equals(data.keySet().toArray()[i])) {
                        links.remove();
                        continue MAIN_LOOP;
                    }
                }

                //reading the source of a site
                URL link = new URL(links.element());
                BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));
                String inputLine;
                String reg_2 = "<a href=\"(/wiki/[a-zA-Z0-9-_:.#()]+)\"(\\s+\\w+=\"[a-zA-Z0-9 ]+\")*>([/a-zA-Z0-9-_ ]+)</a>";
                Pattern pattern = Pattern.compile(reg_2);

                while ((inputLine = in.readLine()) != null) {
                    Matcher matcher = pattern.matcher(inputLine);

                    // if there are some links, then..
                    while (matcher.find()) {

                        // adding it to a queue and to a map
                        System.out.println(matcher.group(1) + " ; " + matcher.group(2) + " ; " + matcher.group(3));
                        String foundLink = "https://en.wikipedia.org" + matcher.group(1);
                        boolean linkIsNotInTheList = true;
                        for (int i = 0; i < data.keySet().size(); i++) {
                            if (foundLink.equals(data.keySet().toArray()[i])) {
                                linkIsNotInTheList = false;
                                break;
                            }
                        }
                        if (linkIsNotInTheList) {
                            links.add(foundLink);
                        }
                        sites.add(foundLink);
                        System.out.println(links.element() + " link to: " + matcher.group(1));

                    }

                }
                in.close();
                data.put(links.element(), sites);
                System.out.println(links.size());
                links.remove();
                maximumQuantity--;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void writeNamesOfSitesToFileForPython(String fileName) {
        String path = "/Users/max/Desktop/" + fileName + ".txt";
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                System.out.println("A NEW FILE WITH ALL NAMES OF LINKED SITES HAS BEEN CREATED!");
            } else System.out.println("A FILE WITH ALL NAMES OF LINKED SITES IS ALREADY EXIST");

            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            bw.write("from graphviz import Digraph\n\ndot = Digraph(edge_attr={'arrowhead': '0', 'arrowtail': '0'})\n\n");

            for (int i = 0; i < data.keySet().size(); i++) {
                String referenceToReadSite = (String)data.keySet().toArray()[i];
                StringBuilder nameOfSite = new StringBuilder();
                int j = 1;
                char temp;
                while ((temp = referenceToReadSite.charAt(referenceToReadSite.length() - j++)) != '/') {
                    nameOfSite.insert(0, temp);
                }
                bw.write("dot.node('" + nameOfSite + "', '" + nameOfSite + "')\n");
                Object [] linksOnSite = data.get(referenceToReadSite).toArray();
                for (int k = 0; k < linksOnSite.length; k++) {
                    String reference = linksOnSite[k].toString();
                    StringBuilder nameOfLinkedSite = new StringBuilder();
                    j = 1;
                    char temp_1;
                    while ((temp_1 = reference.charAt(reference.length() - j++)) != '/') {
                        nameOfLinkedSite.insert(0, temp_1);
                    }
                    bw.write("dot.node('" + nameOfLinkedSite + "', '" + nameOfLinkedSite + "')\n");
                    System.out.println("dot.node('" + nameOfLinkedSite + "', '" + nameOfLinkedSite + "')\n");
                    bw.write("dot.edge('" + nameOfSite + "', '" + nameOfLinkedSite + "')\n");
                    System.out.println("dot.edge('" + nameOfSite + "', '" + nameOfLinkedSite + "')\n");
                }
            }

            bw.write("\n\ndot.render('Desktop/WikiGraph.pdf', view=True)");
            bw.close();

        } catch (Exception e) {
            System.out.println("SOMETHING WENT WRONG :(");
            e.printStackTrace();
        }
    }

    void createDotFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/max/Desktop/newGvFile.dot"));
            bw.write("graph {\na -- b;\nb -- c;\nc -- d;\n a -- c;\n}");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void writeReadSitesToFile(String fileName) {
        String path = "/Users/max/Desktop/" + fileName + ".txt";
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                System.out.println("A NEW FILE WITH ALL REFERENCES OF LINKED SITES HAS BEEN CREATED!");
            } else System.out.println("A FILE WITH ALL REFERENCES OF LINKED SITES IS ALREADY EXIST");

            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            for (int i = 0; i < data.keySet().size(); i++) {

                String referenceToReadSite = (String)data.keySet().toArray()[i];
                bw.write((i+1) + ". " + referenceToReadSite + "\n------------------\n\n");
                Object [] linksOnSite = data.get(referenceToReadSite).toArray();
                for (int k = 0; k < linksOnSite.length; k++) {
                    String reference = linksOnSite[k].toString();

                    bw.write(reference + "\n\n");
                }


            }
            bw.close();

        } catch (Exception e) {
            System.out.println("SOMETHING WENT WRONG :(");
            e.printStackTrace();
        }
    }

    public Map<String, LinkedList<String>> getData() {
        return data;
    }

    public Queue<String> getLinks() {
        return links;
    }

    void scanWebsiteForLinksFixedSize(String url, int quantityOfSites, int limit) {

        try {

            links.add(url);

            MAIN_LOOP:
            while (!links.isEmpty()) {
                LinkedList<String> sites = new LinkedList<>();
                if (quantityOfSites <= 0) return;

                // checking if everything is OK and we don't have any doubles
                for (int i = 0; i < data.keySet().size(); i++) {
                    if (links.element().equals(data.keySet().toArray()[i])) {
                        links.remove();
                        continue MAIN_LOOP;
                    }
                }

                //reading the source of a site
                URL link = new URL(links.element());
                BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));
                String inputLine;
                String reg_2 = "<a href=\"(/wiki/[a-zA-Z0-9-_:.#()]+)\"(\\s+\\w+=\"[a-zA-Z0-9 ]+\")*>([/a-zA-Z0-9-_ ]+)</a>";
                Pattern pattern = Pattern.compile(reg_2);

                while ((inputLine = in.readLine()) != null) {
                    Matcher matcher = pattern.matcher(inputLine);

                    // if there are some links, then..
                    while (matcher.find()) {

                        // adding it to a queue and to a map
                        String foundLink = "https://en.wikipedia.org" + matcher.group(1);
                        boolean linkIsNotInTheList = true;
                        for (int i = 0; i < data.keySet().size(); i++) {
                            if (foundLink.equals(data.keySet().toArray()[i])) {
                                linkIsNotInTheList = false;
                                break;
                            }
                        }
                        if (linkIsNotInTheList) {
                            links.add(foundLink);
                        }
                        sites.add(foundLink);
                        System.out.println(links.element() + " link to: " + matcher.group(1));
                        if (sites.size() > limit) {
                            in.close();
                            data.put(links.element(), sites);
                            System.out.println(links.size());
                            links.remove();
                            quantityOfSites--;
                            continue MAIN_LOOP;
                        }

                    }

                }
                in.close();
                data.put(links.element(), sites);
                System.out.println(links.size());
                links.remove();
                quantityOfSites--;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}