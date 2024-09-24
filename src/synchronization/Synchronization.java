package synchronization;
import java.io.*;
import javax.swing.JTextArea;
import similarityanalysis.*;
import java.util.*;

public class Synchronization
{
    
    public static void main (String [] args)
    {
        /*
        String changefile = "", clonefile = "";      
        int sim = 0;
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
            System.out.println ("Change file path: ");
            changefile = br.readLine ();
            System.out.println ("Clone file path: ");
            clonefile = br.readLine ();
            System.out.println ("Similarity Threshold: ");
            sim = Integer.parseInt(br.readLine ());
        }
        catch (Exception e) {}
        */
        
        /*
        ChangeAnalysis ca = new ChangeAnalysis (changefile, clonefile, sim);
        ca.getchanges ();
        ca.recordchanges();
        ca.analyzechanges ();
        */
    }    
}

// Writer class generates a output file

class Writer
{
    String outputfile = "Updatedoutput.txt"; // output.txt converts into Updatedoutput.txt
    
    public void write (String str)
    {
        try
        {
            BufferedWriter w = new BufferedWriter (new FileWriter (outputfile, true));            
            w.write ("\n"+str);
            w.close ();
        }
        catch (Exception e)
        {
            
        }
    }
    
    public void writeConsole (String str) { System.out.println (str); }
                
    public void createnew (String str)
    {
        try
        {
            BufferedWriter w = new BufferedWriter (new FileWriter (outputfile));            
            w.write ("\n"+str);
            w.close ();
        }
        catch (Exception e)
        {
            
        }
    }    
}


class Fragment
{
    String [] lines = new String[100];
    String filepath = "";
    int start=0, end=0;
    int linecount = 0;
    int revision = 0;
    Writer writer = new Writer ();
    
    
    public void getlines ()
    {
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (filepath)));
            String str = "";
            int line = 0, i=0;
            
            while ((str = br.readLine ()) != null)
            {
                line++;                
                if (line > end) { break; }              
                if (line >= start && line <= end) 
                { 
                    lines[i] = str; 
                    i++;
                }
            }
            linecount = i;
        }
        catch (Exception e)
        {
            //nothing to do.
        }        
    }
    
    public void printlines ()
    {
        writer.write ("-----------fragment----------- start line = "+start + "-------------end line = "+end);
        for (int i =0;i<linecount;i++)
        {
            writer.write (lines[i]);
        }
    }
    
    
    public String getline ()
    {
        String line = "";
        for (int i =0;i<linecount;i++)
        {
            line += " " + lines[i];
        }
        return line.trim();
    }
}

class Change
{
    Fragment fragment1 = new Fragment ();
    Fragment fragment2 = new Fragment ();
    char changetype;
}


class ChangeAnalysis
{
    String [] changestrings = new String[50000];    
    Change [] changes = new Change[20000];
    int changecount = 0;
    
    
    int singleLineMC = 0;
    int doubleLineMC = 0;
    int trippleLineMC = 0;
    int fourLineMC = 0;
    
    
    int fiveLineRC = 0;
    int tenLineRC = 0;
    int fifteenLineRC = 0;
    int twentyLineRC = 0;
    
    String microclonefilepath = "";
    String changefilepath = "";
    int similaritythreshold = 80;
    String systempath = "G:/Masters/systems/ctags";
    
    JTextArea textarea;
    
    Writer writer = new Writer ();
    
    /*ChangeAnalysis (String changefile, String clonefile, int simthresh)
    {
        this.microclonefilepath = clonefile;
        this.changefilepath = changefile;
        this.similaritythreshold = simthresh;
    }*/
    ChangeAnalysis (String syspath, String changepath, int sim, JTextArea tarea)
    {
        this.systempath = syspath;
        this.changefilepath = changepath;
        this.similaritythreshold = sim;
        this.textarea = tarea;
        writer.createnew ("output starts here: ");
    }
    
    void analyzeChanges ()
    {
        int lrev = changes[0].fragment1.revision; // strat 21 version
         
        
        
        int hrev = changes[changecount-1].fragment1.revision; // end 815 version
        
        //appendToTextArea("\n lrev" + lrev + " \n hrev" + hrev);
        
        int start = 0;
        
        int tcm = 0, tcr = 0, csam = 0, ncsam = 0, csar = 0, ncsar = 0;
        
                HashMap<Integer, Integer> countRC = new HashMap<Integer, Integer>();

        
        for (int i =lrev;i<= hrev;i++)
        {
         // i === 21  
           // appendToTextArea ("\n\n\nworking on changes to revision =/..........................working........ ");
          appendToTextArea ("\n\n\nworking on changes to revision = "+i);
            
            Change [] m_changes = new Change[1000];
            Change [] r_changes = new Change[1000];
            
            int mchange_count = 0, rchange_count = 0;
           // appendToTextArea("\n Chage Count " + changecount);
            
            //  Change Count 6642 == ntotal line number of changesCtags.txt file      
        //appendToTextArea("\n print \n" + changes[0].fragment1.revision) == 21 
        
                        
            for (int j = start;j<changecount;j++) 
            {
                
                
                if (changes[j].fragment1.revision > i) { start = j; break; }
                
                if (changes[j].fragment1.revision == i)
                {
                    int rchange = 0, mchange = 0;
                    rchange = inRegularCloneUpdated(changes[j].fragment1);
                    mchange = inMicroCloneUpdated(changes[j].fragment1);
                    int lineNumber = changes[j].fragment1.linecount;

                    if (rchange == 1 && lineNumber > 4) {
                        r_changes[rchange_count] = changes[j]; 
                        rchange_count++;
                        
                        
                        //key,value == line number, total repeation of line number
                        // put == insert
                        
                        if(countRC.containsKey(lineNumber) ){
                            countRC.put(lineNumber, countRC.get(lineNumber)+1);
                            
                            
                        }else  {
                                                    countRC.put(lineNumber,1);
                                }
                        
                        
                    
                    }
                    else { 
                        if (mchange == 1) {
                            m_changes[mchange_count] = changes[j];
                            mchange_count++; 
                            
                            switch (changes[j].fragment1.linecount) {
                            case 1:
                                this.singleLineMC++;
                                break;
                            case 2:
                                this.doubleLineMC++;
                                break;
                            case 3:
                                this.trippleLineMC++;
                                break;
                            case 4:
                                this.fourLineMC++;
                                break;
                            default:
                                break;
                        }
                            

//                        if(changes[j].fragment1.linecount > 4){
//                        appendToTextArea(" \n revission number" + changes[j].fragment1.revision + "\n line count " + changes[j].fragment1.linecount);
//                        break;
//                        }
                            
                            
                        } }
                }
            }
            
            String resultm = analyzeSynchronization(m_changes, mchange_count);
            String resultr = analyzeSynchronization(r_changes, rchange_count);
            
            tcm += mchange_count;
            csam += Integer.parseInt(resultm.split("[,]+")[0].trim());
            ncsam += Integer.parseInt(resultm.split("[,]+")[1].trim());
            
            tcr += rchange_count;
            csar += Integer.parseInt(resultr.split("[,]+")[0].trim());
            ncsar += Integer.parseInt(resultr.split("[,]+")[1].trim());                        
                        
            String str1 = "micro total changes = "+tcm;
            String str2 = "micro changes that can be synchronized automatically = " + csam;
            String str3 = "micro changes that cannot be synchronized automatically = " + ncsam;
            String str4 = "regular total changes = "+tcr;
            String str5 = "regular changes that can be synchronized automatically = " + csar;
            String str6 = "regular changes that cannot be synchronized automatically = " + ncsar;            
            
            
            String str7 = "\n\n"+str1+"\n"+str2+"\n"+str3+"\n"+str4+"\n"+str5+"\n"+str6;
            
            writer.write (str7);
          appendToTextArea (str7);
           
          int totalRegularClone = 0;
          
          for(HashMap.Entry<Integer, Integer> e: countRC.entrySet()){
          
              appendToTextArea("\n Number of line number   "+ e.getKey()+" = total regular clone "+ e.getValue() );
              totalRegularClone += e.getValue();
          }  
          
            String countMicroLine = "\n\n \n number of one line micro-clone = "+ singleLineMC + 
                    " \n number of two line micro-clone =  " + doubleLineMC + "\n number of three line micro-clone = " + trippleLineMC
                    +"\n number of four line micro-clone = " + fourLineMC ;
                    
          appendToTextArea(countMicroLine);
          
          
          // Verification
          
          int totalmicroclone = singleLineMC + doubleLineMC + trippleLineMC + fourLineMC;
          
          if(totalmicroclone == tcm){
          appendToTextArea("\n total micro clone " +totalmicroclone +"  == tcm " + tcm);
          }
          
          if(totalRegularClone == tcr){
          appendToTextArea("\n total regular clone " + totalRegularClone+"  == tcr " + tcr);
          }
          
          
           
        }        
    }
    
    String analyzeSynchronization (Change [] c, int count)
    {
        int csa = 0, ncsa = 0, got = 0;
        
        for (int i =0;i<count;i++)
        {
            got = 0;
            for (int j =0;j<i;j++)
            {
                float sim1 = getsimilarity (changes[i].fragment1.getline(), changes[j].fragment1.getline());
                float sim2 = getsimilarity (changes[i].fragment2.getline(), changes[j].fragment2.getline());                
                
                if (sim1 == 1 && sim2 == 1)
                {
                    
                    writer.write ("\n\nGot similarity, revision = "+changes[i].fragment1.revision);
                    writer.write ("change types = "+changes[i].changetype + " " + changes[j].changetype);
                    changes[i].fragment1.printlines();
                    changes[i].fragment2.printlines();
                    changes[j].fragment1.printlines();
                    changes[j].fragment2.printlines();                                        
                    
                    got = 1;
                    csa++;
                    break;
                }
            }
            if (got == 0) { ncsa++; }
        }
        
        return ""+csa+","+ncsa+"";
    }
    
    void analyzechanges ()
    {
        int similarcount = 0;
        int microclone_changepropagation = 0;
        int regularclone_changepropagation = 0;
        int microclone_change = 0, regularclone_change = 0;
       // System.out.println(" Working Analyze changes");
        
        //csa = change that can be synchronized automatically.
        String csa_micro = "", ncsa_micro = "", csa_regular = "", ncsa_regular = "", tc_micro = "", tc_regular = "";
        
        for (int i=0;i<changecount-1;i++)
        {                        
            String str0 = "\n\n============================"+(i+1)+" of "+changecount;
            appendToTextArea (str0);
            writer.write ("============================"+(i+1)+" of "+changecount);                                  
            
            int inregularclone1 = inRegularCloneUpdated (changes[i].fragment1);
            int inmicroclone1 = inMicroCloneUpdated (changes[i].fragment1);
            
            if (inregularclone1 == 1) { tc_regular += " "+i+" "; }
            else { if (inmicroclone1 == 1) { tc_micro += " " + i+" "; } }
            
            for (int j =i+1;j < changecount && changes[i].fragment1.revision <= changes[j].fragment1.revision;j++)
            {
                if (changes[i].fragment1.revision == changes[j].fragment1.revision)
                {     
                    //checking if the change occurred in regular clones.                    
                    int inregularclone2 = inRegularCloneUpdated (changes[j].fragment1);    

                    //checking if the change occurred in micro-clones                    
                    int inmicroclone2 = inMicroCloneUpdated (changes[j].fragment1); 
                    
                    
                    if (inregularclone1 == 1 && inregularclone2 == 1) { regularclone_change++; }                     
                    else
                    {                    
                        if (inmicroclone1 == 1 && inmicroclone2 == 1) { microclone_change++; }
                    }                   
                    
                    
                    float sim1 = getsimilarity (changes[i].fragment1.getline(), changes[j].fragment1.getline());
                    float sim2 = getsimilarity (changes[i].fragment2.getline(), changes[j].fragment2.getline());
                    
                    if (sim1 == 1 && sim2 == 1)
                    {
                        writer.write ("got similarity ----------------------------- ");
                        writer.write ("change types = "+changes[i].changetype + " " + changes[j].changetype);
                        changes[i].fragment1.printlines();
                        changes[i].fragment2.printlines();
                        
                        changes[j].fragment1.printlines();
                        changes[j].fragment2.printlines();
                        
                        if (inregularclone1 == 1 && inregularclone2 == 1) 
                        { 
                            regularclone_changepropagation++; 
                            if (!csa_regular.contains (" "+j+" ")) { csa_regular += " "+j+" "; }
                            if (!csa_regular.contains (" "+i+" ") && !ncsa_regular.contains (" "+i+" ")) { ncsa_regular += " " + i + " "; }                            
                        }                        
                        else
                        {
                            if (inmicroclone1 == 1 && inmicroclone2 == 1) 
                            { 
                                microclone_changepropagation++; 
                                if (!csa_micro.contains (" "+j+" ")) { csa_micro += " "+j+" "; }
                                if (!csa_micro.contains (" "+i+" ") && !ncsa_micro.contains (" "+i+" ")) { ncsa_micro += " " + i + " "; }
                            }
                        }
                        similarcount++;
                    }
                    else
                    {
                        writer.write ("not similar");
                    }
                }
            }
            
            String str1 = "similar count = "+similarcount;
            String str2 = "count of changes in micro-clones = "+microclone_change;
            String str3 = "count of changes in regular clones = "+regularclone_change;
            String str4 = "micro clone change propagation possible = "+microclone_changepropagation;
            String str5 = "regular clone change propagation possible = "+regularclone_changepropagation;
            
            String str6 = "\n\n" + str1 + "\n" + str2 + "\n" + str3 + "\n" + str4 + "\n" + str5;
            
            String str11 = "micro total changes = "+tc_micro.trim().split("[ ]+").length;
            String str7 = "micro changes that can be synchronized automatically = " + csa_micro.trim().split("[ ]+").length;
            String str8 = "micro changes that cannot be synchronized automatically = " + ncsa_micro.trim().split("[ ]+").length;
            String str12 = "regular total changes = "+tc_regular.trim().split("[ ]+").length;
            String str9 = "regular changes that can be synchronized automatically = " + csa_regular.trim().split("[ ]+").length;
            String str10 = "regular changes that cannot be synchronized automatically = " + ncsa_regular.trim().split("[ ]+").length;
            
            String str13 = "\n\n"+str11+"\n"+str7+"\n"+str8+"\n"+str12+"\n"+str9+"\n"+str10;
            
            writer.write (str6);
            writer.write (str13);

            appendToTextArea (str13);
            
            
            
        }
        //writer.writeConsole ("similar count = "+similarcount);
        //writer.writeConsole ("count of changes in micro-clones = "+microclone_change);
        //writer.writeConsole ("count of changes in regular clones = "+regularclone_change);
        //writer.writeConsole ("micro clone change propagation possible = "+microclone_changepropagation);
        //writer.writeConsole ("regular clone change propagation possible = "+regularclone_changepropagation);                    
    }
    
    
    public void appendToTextArea (String str)
    {
        textarea.append (str);
        textarea.setCaretPosition(textarea.getDocument().getLength());
    }
    
    
    int makePair (int cloneid1, int cloneid2, int revision)
    {
        String pairfilepath = microclonefilepath.replaceAll ("s.csv", "pairs.csv");
        
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (pairfilepath)));
            String str = "";
            
            br.readLine ();
            while ((str = br.readLine ())!= null)
            {
                int cid1 = Integer.parseInt (str.split("[,]+")[0].trim());
                int cid2 = Integer.parseInt (str.split("[,]+")[1].trim());
                int rev = Integer.parseInt (str.split("[,]+")[2].trim());
                
                if (rev == revision && cloneid1 == cid1 && cloneid2 == cid2) {return 1;}
                if (rev == revision && cloneid2 == cid1 && cloneid1 == cid2) {return 1;}
            }
            
        }
        catch (Exception e) {  }
        
        
        return 0;
    }
    
    String getFragmentFilePath (Fragment f)
    {
        int frev = f.revision;
        String ffilepath = f.filepath;
        int fsline = f.start;
        int feline = f.end;
        String rstring = "version-"+frev;            
        int ind = ffilepath.indexOf(rstring)+ rstring.length();
        ffilepath = ffilepath.substring (ind+1);      
        String fragmentfilepath = rstring + "/" + ffilepath;
        fragmentfilepath = fragmentfilepath.replaceAll("\\\\", "/");
        
        return fragmentfilepath;
    }
    
    int inMicroCloneUpdated (Fragment fr1)
    {
        int frev = fr1.revision;
        String fragmentfilepath1 = getFragmentFilePath (fr1);
        
       // appendToTextArea(frev+ "\n");
       // appendToTextArea(fragmentfilepath1+ "\n"); 
        
        // output of fragmentfilepath1 == /version-2/python.c 
        
        String clonefilepath = systempath + "/repository/version-"+frev+"_blocks-blind-clones/version-"+frev+"_blocks-blind-clones-0.30.xml";
         //appendToTextArea(clonefilepath+ "\n"); 
        try
        {
            String str = "";
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (clonefilepath)));
            
            
            //<clone nlines="28" similarity="73"> ---clone nlines
//<source file="G:/Masters/systems/ctags/repository/version-2/fortran.c" sl1---startline="1363" el1----endline="1382" pcid="272"/> ----str1
//<source file="G:/Masters/systems/ctags/repository/version-2/fortran.c" sl2----startline="1608" el2----endline="1624" pcid="291"/>-----str2
//</clone>
            
            while ((str = br.readLine ())!= null)
            {
                               
                if (str.contains ("<clone nlines="))
                {
                    String str1 = br.readLine ();
                      //appendToTextArea(str1+ "\n");
                    
                    String str2 = br.readLine ();
                     //appendToTextArea(str2+ "\n");
                    
                    
                    String f1 = "", f2 = "";
                    int sl1 = 0, sl2 = 0, el1 = 0, el2 = 0;
                    
                     //appendToTextArea(str1 + "\n");
                    f1 = str1.split ("[\"]+")[1].trim();
                    
                    // output of f1 = G:/Masters/systems/ctags/repository/version-2/fortran.c
                    
                    // appendToTextArea(f1 + "\n");
                    //start line == sl
                    //end line == el
                    sl1 = Integer.parseInt (str1.split ("[\"]+")[3].trim());
                    el1 = Integer.parseInt (str1.split ("[\"]+")[5].trim());
                    
                    f2 = str2.split ("[\"]+")[1].trim();
                    sl2 = Integer.parseInt (str2.split ("[\"]+")[3].trim());
                    el2 = Integer.parseInt (str2.split ("[\"]+")[5].trim());
                    
                    if (el1-sl1+1 > 4) { continue; } //checking micro-clones
                    if (el2-sl2+1 > 4) { continue; } //checking micro-clones                    
                                                            
                   
                    
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.end <= el1) { 
//                        
//                       if(fr1.linecount>4){
//                       appendToTextArea(" \n " +" \n start line1 " + sl1 + " \n  end line 1" +el1 + " \n  fragment file path1"
//                               +fragmentfilepath1 + " \n file path xml " +f1 + 
//                               " \n line count" +fr1.linecount +" \n revission" + fr1.revision);
                        // }
//                       int numberOfLines1 = el1-sl1+1;
//                       
//                        switch (numberOfLines1) {
//                            case 1:
//                                this.singleLineMC++;
//                                break;
//                            case 2:
//                                this.doubleLineMC++;
//                                break;
//                            case 3:
//                                this.trippleLineMC++;
//                                break;
//                            case 4:
//                                this.fourLineMC++;
//                                break;
//                            default:
//                                break;
//                        }
                        //singleLineMC++;
                        
                       
                        return 1; }
                    
                    if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.end <= el2) { 
                        
                                //singleLineMC++;  
                        
//                        if(fr1.linecount>4){
//                       appendToTextArea(" \n " +" \n start line1 = " + sl2 + " \n  end line 1= " +el2 + " \n  fragment file path1 = "
//                               +fragmentfilepath1 + " \n file path 2 xml = " +f2 + 
//                               " \n line count =  " +fr1.linecount +" \n revission = " + fr1.revision);
//                       
//                       
//                       }
                        

//                      int numberOfLines2 = el2-sl2+1;
//                       
//                        switch (numberOfLines2) {
//                            case 1:
//                                this.singleLineMC++;
//                                break;
//                            case 2:
//                                this.doubleLineMC++;
//                                break;
//                            case 3:
//                                this.trippleLineMC++;
//                                break;
//                            case 4:
//                                this.fourLineMC++;
//                                break;
//                            default:
//                                break;
//                        }
                       
                        
                        return 1; } 
                    
                    
                    
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            writer.write (e.toString());
        }
        return 0;
    }
    
    
    int inMicroClone (Fragment fr1, Fragment fr2)
    {
        int frev = fr1.revision;
        String fragmentfilepath1 = getFragmentFilePath (fr1);
        String fragmentfilepath2 = getFragmentFilePath (fr2);
        
        String clonefilepath = systempath + "/repository/version-"+frev+"_blocks-blind-clones/version-"+frev+"_blocks-blind-clones-0.30.xml";
        
        try
        {
            String str = "";
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (clonefilepath)));
            
            while ((str = br.readLine ())!= null)
            {
                if (str.contains ("<clone nlines="))
                {
                    String str1 = br.readLine ();
                    String str2 = br.readLine ();
                    
                    String f1 = "", f2 = "";
                    int sl1 = 0, sl2 = 0, el1 = 0, el2 = 0;
                    
                    f1 = str1.split ("[\"]+")[1].trim();
                    sl1 = Integer.parseInt (str1.split ("[\"]+")[3].trim());
                    el1 = Integer.parseInt (str1.split ("[\"]+")[5].trim());
                    
                    f2 = str2.split ("[\"]+")[1].trim();
                    sl2 = Integer.parseInt (str2.split ("[\"]+")[3].trim());
                    el2 = Integer.parseInt (str2.split ("[\"]+")[5].trim());
                    
                    if (el1-sl1+1 > 4) { continue; } //checking micro-clones
                    if (el2-sl2+1 > 4) { continue; } //checking micro-clones
                    
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.start <= el1)
                    {
                        if (f2.contains (fragmentfilepath2) && fr2.start >= sl2 && fr2.start <= el2)
                        {
                            return 1;
                        }
                    }
                    
                    if (f1.contains (fragmentfilepath2) && fr2.start >= sl1 && fr2.start <= el1)
                    {
                        if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.start <= el2)
                        {
                            return 1;
                        }
                    }    
                    
                    
                    /*
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.start <= el1) { return 1; }
                    if (f1.contains (fragmentfilepath2) && fr2.start >= sl1 && fr2.start <= el1) { return 1; }
                    if (f2.contains (fragmentfilepath2) && fr2.start >= sl2 && fr2.start <= el2) { return 1; }
                    if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.start <= el2) { return 1; }
                    */
                    
                    
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            writer.write (e.toString());
        }
        return 0;
    }
    
    int inRegularCloneUpdated (Fragment fr1)
    {
        int frev = fr1.revision;
        String fragmentfilepath1 = getFragmentFilePath (fr1);
        
        String clonefilepath = systempath + "/repository/version-"+frev+"_blocks-blind-clones/version-"+frev+"_blocks-blind-clones-0.30.xml";
        
        try
        {
            String str = "";
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (clonefilepath)));
            
            while ((str = br.readLine ())!= null)
            {
                if (str.contains ("<clone nlines="))
                {
                    String str1 = br.readLine ();
                    String str2 = br.readLine ();
                    
                    String f1 = "", f2 = "";
                    int sl1 = 0, sl2 = 0, el1 = 0, el2 = 0;
                    
                    f1 = str1.split ("[\"]+")[1].trim();
                    sl1 = Integer.parseInt (str1.split ("[\"]+")[3].trim());
                    el1 = Integer.parseInt (str1.split ("[\"]+")[5].trim());
                    
                    f2 = str2.split ("[\"]+")[1].trim();
                    sl2 = Integer.parseInt (str2.split ("[\"]+")[3].trim());
                    el2 = Integer.parseInt (str2.split ("[\"]+")[5].trim());
                    
                    if (el1-sl1+1 <= 4) { continue; } //checking regular-clones
                    if (el2-sl2+1 <= 4) { continue; } //checking regular-clones 
                    
                    
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.end <= el1) { return 1; }
                    if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.end <= el2) { return 1; }
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            
        }
        return 0;
    }
    
    
    int inRegularClone (Fragment fr1, Fragment fr2)
    {
        int frev = fr1.revision;
        String fragmentfilepath1 = getFragmentFilePath (fr1);
        String fragmentfilepath2 = getFragmentFilePath (fr2);
        
        String clonefilepath = systempath + "/repository/version-"+frev+"_blocks-blind-clones/version-"+frev+"_blocks-blind-clones-0.30.xml";
        
        try
        {
            String str = "";
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (clonefilepath)));
            
            while ((str = br.readLine ())!= null)
            {
                if (str.contains ("<clone nlines="))
                {
                    String str1 = br.readLine ();
                    String str2 = br.readLine ();
                    
                    String f1 = "", f2 = "";
                    int sl1 = 0, sl2 = 0, el1 = 0, el2 = 0;
                    
                    f1 = str1.split ("[\"]+")[1].trim();
                    sl1 = Integer.parseInt (str1.split ("[\"]+")[3].trim());
                    el1 = Integer.parseInt (str1.split ("[\"]+")[5].trim());
                    
                    f2 = str2.split ("[\"]+")[1].trim();
                    sl2 = Integer.parseInt (str2.split ("[\"]+")[3].trim());
                    el2 = Integer.parseInt (str2.split ("[\"]+")[5].trim());
                    
                    if (el1-sl1+1 <= 4) { continue; } //checking regular-clones
                    if (el2-sl2+1 <= 4) { continue; } //checking regular-clones
                    
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.start <= el1)
                    {
                        if (f2.contains (fragmentfilepath2) && fr2.start >= sl2 && fr2.start <= el2)
                        {
                            return 1;
                        }
                    }
                    
                    if (f1.contains (fragmentfilepath2) && fr2.start >= sl1 && fr2.start <= el1)
                    {
                        if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.start <= el2)
                        {
                            return 1;
                        }
                    }    
                    
                    
                    /*
                    if (f1.contains (fragmentfilepath1) && fr1.start >= sl1 && fr1.start <= el1) { return 1; }
                    if (f1.contains (fragmentfilepath2) && fr2.start >= sl1 && fr2.start <= el1) { return 1; }
                    if (f2.contains (fragmentfilepath2) && fr2.start >= sl2 && fr2.start <= el2) { return 1; }
                    if (f2.contains (fragmentfilepath1) && fr1.start >= sl2 && fr1.start <= el2) { return 1; }
                    */
                    
                    
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            
        }
        return 0;
    }
    
    
    
    int isInMicroClone (Fragment f)
    {
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (microclonefilepath)));
            String str = "";
            
            
            int frev = f.revision;
            String ffilepath = f.filepath;
            int fsline = f.start;
            int feline = f.end;
            String rstring = "version-"+frev;            
            int ind = ffilepath.indexOf(rstring)+ rstring.length();
            ffilepath = ffilepath.substring (ind+1);
            
            
            
            //ffilepath = ffilepath.substring (rstring.length()+1);
            
            br.readLine ();
            while ((str = br.readLine ())!= null)
            {
                int rev = Integer.parseInt(str.split ("[,]+")[0].trim());
                String filepath = str.split ("[,]+")[1].trim();
                int sline = Integer.parseInt(str.split ("[,]+")[2].trim());
                int eline = Integer.parseInt(str.split ("[,]+")[3].trim());
                int cloneid = Integer.parseInt(str.split ("[,]+")[4].trim());
                
                if (ffilepath.equals (filepath) && fsline >= sline && feline <= eline)
                {
                    return cloneid;
                }
            }
        }
        catch (Exception e) {}
        
        return -1;
    }
    
    float getsimilarity (String str1, String str2)
    {
        //SimilarityAnalysis sa = new SimilarityAnalysis ();
        //double sim = sa.getStrikeAMatch(str1, str2);
        
        //if (sim >= this.similaritythreshold) { return 1; }
        
        if (str1.equals(str2)) { return 1; }
        
        return 0;
    }
    
    //ChangesCtags.txt file line by line read korbe and  changestrings[changecount] = str or array te save korbe line gula index wise. Besides, total file e kotogula line ache segula count korbe;

    void getchanges ()
    {
        String filepath = this.changefilepath;
        
        //appendToTextArea(filepath + "\n");
        //Output of the filePath is ChangesCtags.txt
        
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (filepath)));
            String str = "";

            while ((str = br.readLine ()) != null)
            {
                //appendToTextArea(str + "\n");
                if (str.trim().length() > 0)
                {
                    
                    changestrings[changecount] = str;
                    changecount++;
                }
            }
        }
        catch (Exception e)
        {

        }
    }
    
    // ChangesCtags file e total kotogula line ache ta changecount[] array te save thakbe

    void recordchanges ()
    {
        for (int i =0;i<changecount;i++)
        {
            changes[i] = new Change ();
            recordchange (i);
            
            // after calling recordchanges we got both new version 22 and old version 21 of 
            // cfilepath1;
        //start = s1;
        //end = e1;
            
            
            
            changes[i].fragment1.getlines();
            changes[i].fragment2.getlines();
            changes[i].fragment1.revision = getrevision (changes[i].fragment1.filepath);
            changes[i].fragment2.revision = getrevision (changes[i].fragment2.filepath);
        }
    }
    
    int getrevision (String path)
    {
        int t = path.indexOf ("version-")+8;
        String vstring = "";
        
        path = path.replaceAll("\\\\", "/");
        
        for (int i=t;i<1000;i++)
        {
            if (path.charAt (i) == '/') { break; }
            vstring += path.charAt (i);
        }
        int v = Integer.parseInt (vstring);
        
        
        return v;
    }

    void recordchange (int cindex)
    {
        String change = changestrings[cindex];
        
        // G:\Masters\systems\ctags\repository\version-21\regex.c; 2c2
        
        String cfilepath = change.split("[;]+")[0].trim ();
        String temp = change.split("[;]+")[1].trim ();
        String temp1 = "", temp2 = "";
        String separator = "";
        
        // start = s, end = e
        int s1, e1, s2, e2;
        
        if (temp.contains ("a")) { 
            separator = "[a]+"; 
        changes[cindex].changetype = 'a';
        }
        if (temp.contains ("c")) { separator = "[c]+"; changes[cindex].changetype = 'c';}
        
        if (temp.contains ("d")) { separator = "[d]+"; changes[cindex].changetype = 'd';}

        temp1 = temp.split (separator)[0];
        temp2 = temp.split (separator)[1];
        
        if (temp1.contains(",")) 
        { 
            s1 = Integer.parseInt (temp1.split("[,]+")[0]); 
            e1 = Integer.parseInt (temp1.split("[,]+")[1]);
        }
        else
        {
            s1 = Integer.parseInt (temp1);
            e1 = Integer.parseInt (temp1);
        }
        
        if (temp2.contains(",")) 
        { 
            s2 = Integer.parseInt (temp2.split("[,]+")[0]); 
            e2 = Integer.parseInt (temp2.split("[,]+")[1]);
        }
        else
        {
            s2 = Integer.parseInt (temp2);
            e2 = Integer.parseInt (temp2);
        }        
        
        String cfilepath2 = "";
        String cfilepath1 = cfilepath;
        String vstring = "";

        int v1 = getrevision (cfilepath1);
        int v2 = v1+1;
        vstring = "version-"+v1;
        String vstring2 = "version-"+v2;
        cfilepath2 = cfilepath.replaceAll(vstring, vstring2);
        
 // cfilepath1 = G:\Masters\systems\ctags\repository\version-21\regex.c, 2, 2  
//cfilepath2 = G:\Masters\systems\ctags\repository\version-22\regex.c, 2, 2
// Actually version 21 e j change ashce oita save korche and oi change hober jonno version 21 tokhon version 22 hosce
        
        
        //now we have got the followings.
        //cfilepath1, s1, e1
        //cfilepath2, s2, e2
        
        writer.write(cfilepath1 + ", "+s1 + ", " + e1);
        writer.write(cfilepath2 + ", "+s2 + ", " + e2);
        
        // comment 824 to 827
        //appendToTextArea("\n"+cfilepath1 + ", "+s1 + ", " + e1);
        //appendToTextArea("\n"+cfilepath2 + ", "+s2 + ", " + e2);
        
        
        
        // fragment1 hosce old version - ex- version 21
        changes[cindex].fragment1.filepath = cfilepath1;
        changes[cindex].fragment1.start = s1;
        changes[cindex].fragment1.end = e1;
        
        //fragment2 hosce new version - ex- version 22 
        
        changes[cindex].fragment2.filepath = cfilepath2;
        changes[cindex].fragment2.start = s2;
        changes[cindex].fragment2.end = e2;        
    }    
}