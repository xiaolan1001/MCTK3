package swing;

import edu.wis.jtlv.env.Env;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Vector;

import static swing.EditorJPanel.modelTextPane;
import static swing.MCTKFrame.*;


public class FileOperation {
    JFileChooser filechoose = new JFileChooser();
    MCTKFrame mainFrame;
    public static String currentPathFileName ="";
    String fileName;

    public FileOperation(MCTKFrame mainFrame) {
        this.mainFrame = mainFrame;
        filechoose.setAcceptAllFileFilterUsed(false);
        filechoose.addChoosableFileFilter(new MyFileFilter("SMV Code(.smv)", ".smv"));//����ļ�������
        FileSystemView fsv = FileSystemView.getFileSystemView();
        filechoose.setCurrentDirectory(fsv.getHomeDirectory());//����Ĭ��·��Ϊ����·��
    }

    public boolean open() {
        if (filechoose.showOpenDialog(mainFrame)==JFileChooser.APPROVE_OPTION) {
            File newFile;
            File file = filechoose.getSelectedFile();
            if (file.getName() == null) return false;
            BufferedReader br;
            newFile = file;
            currentPathFileName = newFile.toString();
            fileName = newFile.getName();
            setFrameTitle(newFile);
            try {
                String s;
                StringBuffer sbf = new StringBuffer();
                br = new BufferedReader(new FileReader(newFile));
                while ((s = br.readLine()) != null) {
                    sbf.append(s + "\r\n");
                }
                br.close();
                if (sbf.length() > 2)
                    sbf = new StringBuffer(sbf.substring(0, sbf.length() - 2));
                String content = sbf.toString().replaceAll("\\t", "   ");

                //String moduleStr=new String();
                Vector<String[]> moduleSpecAnns=new Vector<String[]>();
                Env.seperateSpecsFromSMVfile(content, moduleSpecAnns);

                modelTextPane.setText(moduleSpecAnns.get(0)[0]);
                MCTKFrame.modelTextPaneChanged=false;

                if (initSpecAnns!=null) initSpecAnns.clear(); else initSpecAnns=new Vector<String[]>();
                for(int i=1; i<moduleSpecAnns.size(); i++) {
                    String[] specAnn=moduleSpecAnns.get(i);
//                    specAnn[0]=specAnn[0].replaceAll("RTCTL\\*SPEC","").trim();
//                    specAnn[0]=specAnn[0].replaceAll("RTCDL\\*SPEC","").trim();
                    //if(specAnn[0]=="") specAnn[0]="CTL";
                    initSpecAnns.add(specAnn);
                }

                specsTableModel.getDataVector().clear();
                for(int i=0; i<initSpecAnns.size(); i++) {
                    //mainFrame.insertSpec(i,"RTCDL*",initSpecAnns.get(i)[0],initSpecAnns.get(i)[1]);
                    mainFrame.insertSpec(i,initSpecAnns.get(i)[0],initSpecAnns.get(i)[1],initSpecAnns.get(i)[2]);
                }
                if(specsTable.getRowCount()>0) specsTable.setRowSelectionInterval(0,0);

                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public boolean saveFile() throws IOException {
        if (!currentPathFileName.equals(""))
        {
            if(!MCTKFrame.modelTextPaneChanged && !mainFrame.specsTableChanged()){
                consoleOutput(0,"warning", "There is not any change in the model and specification list. Don't need to save the opening file.\n");
                return true;
            }

            BufferedWriter br;
            File newFile = new File(currentPathFileName);
            setFrameTitle(newFile);
            //save file
            String fileContent=mainFrame.generateSMVtext(false);
            if(fileContent!=null){
                br = new BufferedWriter(new FileWriter(newFile));
                br.write(fileContent);
                br.flush();
                br.close();

                if(specsTableChanged()) {
                    initSpecAnns.clear();
                    for (int row=0; row<specsTableModel.getRowCount(); row++) {
                        String[] specAnn=new String[3];
                        specAnn[0]=((String) specsTableModel.getValueAt(row,colLogic)).trim();
                        specAnn[1]=((String) specsTableModel.getValueAt(row,colSpec)).trim();
                        specAnn[2]=((String) specsTableModel.getValueAt(row,colAnnotation)).trim();
                        initSpecAnns.add(specAnn);
                    }
                }

                modelTextPaneChanged=false;
                consoleOutput(0,"emph", "The file "+newFile.getName()+" saved.\n");
                return true;
            }else return false;
        } else
        {
            consoleOutput(0,"emph", "Please use the \"Save As\" function.\n");
            return false;
        }
    }

    // save to a file before verification
    // return true if the file is ok for verification
    // return false if the file is not ready for verification
    public boolean saveFile4verification() throws IOException {
        if (!currentPathFileName.equals(""))
        {
            if(!MCTKFrame.modelTextPaneChanged && !mainFrame.specsTableChanged()){
                return true;
            }
            Object[] options = {"Save", "Cancel"};
            int responseSave = JOptionPane.showOptionDialog(mainFrame, "Do you want to save the editing model and specifications to "+fileName+"?\n",
                    "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (responseSave != 0) // do not save
                return false; //cancel

            // need to save
            BufferedWriter br;
            File newFile = new File(currentPathFileName);
            setFrameTitle(newFile);
            //save file
            String fileContent=mainFrame.generateSMVtext(false);
            if(fileContent!=null){
                br = new BufferedWriter(new FileWriter(newFile));
                br.write(fileContent);
                br.flush();
                br.close();

                if(specsTableChanged()) {
                    initSpecAnns.clear();
                    for (int row = 0; row < specsTableModel.getRowCount(); row++) {
                        String[] specAnn=new String[3];
                        specAnn[0]=((String) specsTableModel.getValueAt(row,colLogic)).trim();
                        specAnn[1]=((String) specsTableModel.getValueAt(row,colSpec)).trim();
                        specAnn[2]=((String) specsTableModel.getValueAt(row,colAnnotation)).trim();
                        initSpecAnns.add(specAnn);
                    }
                }

                modelTextPaneChanged=false;
                consoleOutput(0,"emph", "The file "+newFile.getName()+" saved.\n");
                return true;
            }else return false;
        } else
        {
            consoleOutput(0,"emph", "Please save the editing model to a file before verification.\n");
            return false;
        }
    }

    public boolean SaveAs() throws IOException { // return 0--OK, return 1--Cancel
        if (filechoose.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            // 1. input/choose a file name to save
            File newFile;
            File file = filechoose.getSelectedFile();
            if (file.getName() == null) return false;
            BufferedWriter br;
            MyFileFilter filter = (MyFileFilter) filechoose.getFileFilter();
            String ends = filter.getEnds();
            if (file.toString().indexOf(ends) != -1) {
                newFile = file;
            } else {
                newFile = new File(file.getAbsolutePath() + ends);
            }
            // 2. confirm that use the chosen file name to save the current data
            int responseReplace;
            if (newFile.exists())
            {
                Object[] options = {"Replace", "Cancel"};
                responseReplace = JOptionPane.showOptionDialog(mainFrame, "A file with the same name "+fileName+" already exists!\n" +
                                "Do you want to replace it?",
                        "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (responseReplace != 0) // do not replace
                    return false; //cancel
            }
            // try to replace the file
            //save file
            String fileContent=mainFrame.generateSMVtext(false);
            if(fileContent!=null){
                if(newFile.exists()) newFile.delete();

                br = new BufferedWriter(new FileWriter(newFile));
                br.write(fileContent);
                br.flush();
                br.close();

                if(specsTableChanged()) {
                    initSpecAnns.clear();
                    for (int row = 0; row < specsTableModel.getRowCount(); row++) {
                        String[] specAnn=new String[3];
                        specAnn[0]=((String) specsTableModel.getValueAt(row,colLogic)).trim();
                        specAnn[1]=((String) specsTableModel.getValueAt(row,colSpec)).trim();
                        specAnn[2]=((String) specsTableModel.getValueAt(row,colAnnotation)).trim();
                        initSpecAnns.add(specAnn);
                    }
                }

                modelTextPaneChanged=false;
                currentPathFileName = newFile.toString();
                fileName = newFile.getName();
                setFrameTitle(newFile);

                return true;
            }else return false;
        }else return false;
    }


    public boolean creat() {
        if (JFileChooser.APPROVE_OPTION == filechoose.showSaveDialog(mainFrame)) {
            File newFile;
            File file = filechoose.getSelectedFile();
            if (file.getName() == null) return false;
            BufferedWriter br;
            MyFileFilter filter = (MyFileFilter) filechoose.getFileFilter();
            String ends = filter.getEnds();
            if (file.toString().indexOf(ends) != -1) {
                newFile = file;
            } else {
                newFile = new File(file.getAbsolutePath() + ends);
            }
            currentPathFileName = newFile.toString();
            fileName = newFile.getName();
            if (newFile.exists())
            {
                Object[] options = {"Replace", "Rename"};
                int response = JOptionPane.showOptionDialog(mainFrame, "A file with the same name already exists!",
                        "Warning", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0) {//Replace
                    newFile.delete();
                    // SaveAs();
                } else if (response == 1) {//Rename
                    creat();
                }
            }
            setFrameTitle(newFile);
            modelTextPane.setText("");
            try {
                br = new BufferedWriter(new FileWriter(newFile));
                br.write("");
                br.flush();
                br.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public void setFrameTitle(File file) {
        String name = file.getName();
        mainFrame.setTitle("MCTK 3.0 - " + file.getAbsolutePath());
    }

    public String getPath() {
        String name = getFileName();
        int t = currentPathFileName.lastIndexOf(name, currentPathFileName.length() - 1);
        String path = currentPathFileName.substring(0, t);
        return path;
    }

    public String getFileName() {
        if (fileName == null) return "";
        int point = fileName.indexOf(".");
        String name = fileName.substring(0, point);
        return name;
    }

    class MyFileFilter extends FileFilter {

        String ends; // �ļ���׺
        String description; // �ļ�����

        public MyFileFilter(String description, String ends) {
            this.ends = ends; // �����ļ���׺
            this.description = description; // �����ļ���������
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            String fileName = f.getName();
            if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;//�ж��ļ�����׺
            return false;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        public String getEnds() {
            return this.ends;
        }

    }
}
