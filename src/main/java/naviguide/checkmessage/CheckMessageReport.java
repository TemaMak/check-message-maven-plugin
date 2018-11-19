package naviguide.checkmessage;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.maven.plugin.logging.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.doxia.sink.Sink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;


@Mojo(
        name = "check-message",
        defaultPhase = LifecyclePhase.SITE,
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        requiresProject = true,
        threadSafe = true
        )
public class CheckMessageReport  extends AbstractMavenReport
{

	public String getDescription(Locale arg0) {
		return "simple description";
	}

	public String getName(Locale arg0) {
		return "Check message report";
	}

	public String getOutputName() {
		return "check-message-report";
	}

	public List<File> addFiles(List<File> files, File dir)
	{
	    if (files == null)
	        files = new LinkedList<File>();

	    if (!dir.isDirectory())
	    {
	        files.add(dir);
	        return files;
	    }

	    for (File file : dir.listFiles())
	        addFiles(files, file);
	    return files;
	}
	
	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
        // Get the logger
		Log logger = getLog();

        // Some info
        logger.info("Generating " + getOutputName() + ".html"
                        + " for " + project.getName() + " " + project.getVersion());


        MessageProblemHolder withOutText = new MessageProblemHolder();
        
        File dir = new File("./src/main/webapp/WEB-INF/views/");
        List<File> addFiles = addFiles(null,dir);
        
        for(File f: addFiles){
        	try {
				logger.info("process " + f.getCanonicalPath().toString());				
				Document doc = Jsoup.parse(f,"UTF-8");
				for (Element message : doc.getElementsByTag("spring:message")){
					String code = message.attr("code");
					if(
						StringUtils.isNotBlank(code)
						&& StringUtils.isBlank(message.attr("text"))
					){
						withOutText.addProblem(f, code);
					}
					logger.info(message.attr("code"));
        		}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        Sink mainSink = getSink();
        if (mainSink == null) {
                throw new MavenReportException("Could not get the Doxia sink");
        }

        // Page title
        mainSink.head();
        mainSink.title();
        mainSink.text("Simple Report for " + project.getName() + " " + project.getVersion());
        mainSink.title_();
        mainSink.head_();

        mainSink.body();

        // Heading 1
        mainSink.section1();
        mainSink.sectionTitle1();
        mainSink.text("Simple Report for " + project.getName() + " " + project.getVersion());
        mainSink.sectionTitle1_();

        // Content
        mainSink.paragraph();
        mainSink.text("This page provides simple information, like its location: ");
        mainSink.text(project.getBasedir().getAbsolutePath());
        mainSink.paragraph_();
                        
        // Close
    	mainSink.section1_();
    	
    	//Message without text alternative
    	mainSink.section2();
    	mainSink.sectionTitle1();
    	mainSink.text("Message with out text-attribute");
    	mainSink.sectionTitle1_();
    	
    	for(Entry<String,List<String>> filesProblem: withOutText.entrySet()){
        	mainSink.sectionTitle2();
        	mainSink.text(filesProblem.getKey());
        	mainSink.sectionTitle2_(); 
        	
        	mainSink.table();
        	for(String messageCode: filesProblem.getValue()){
        		mainSink.tableRow();
        		mainSink.tableCell();
        		mainSink.text(messageCode);
        		mainSink.tableCell_();
        		mainSink.tableRow_();        		
        	}
        	mainSink.table_();
    	}
    	
        mainSink.body_();	
	}

}
