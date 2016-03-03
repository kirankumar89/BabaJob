package BabaJob.JobSeeker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;

public class JobSeekerRegistration {
	
	WebDriver d;	
	String BabaJobHomePage = "http://www.babajob.com";
	String Name = "Baba Employment Seeker";
	String Email = "kiran.kumar.sdet@gmail.com"; 
	String Preference1 = "Driver";
	String Preference2 = "Cook";
	String Password = "Something"; // Set the same password as of the email account, I am using the same as Auth credentials for JAVA X mailer

	
	
	@BeforeSuite
	public void SetUp()
	{
		
		d = new FirefoxDriver();
		d.manage().window().maximize();
		
	}
	
	@AfterSuite
	public void TearDown()
	{
		
		//d.quit();
		
	}
	
	@Test(priority = 1)
	public void JobSeekerSignUp() throws InterruptedException
	{
		//Goto Babajob.com and assert if Home Page is launched successfully
		d.get(BabaJobHomePage);
		
		WebElement RegisterNowButton = d.findElement(By.id("findJobs"));
		Assert.assertEquals(RegisterNowButton.isDisplayed(), true);
		Reporter.log("Logged into Babajob.com successfully", true);
		
		//Click on Register Now and verify if inside the registration page
		RegisterNowButton.click();
		
		WebElement JobSeekerName = d.findElement(By.id("jobseekerName"));
		WebElement jobseekerMobileOrEmail = d.findElement(By.id("jobseekerMobileOrEmail"));
		WebElement desiredcategory = d.findElement(By.id("field-desiredcategory"));
		WebElement userSecondPreferredCategory = d.findElement(By.id("field-userSecondPreferredCategory"));
		WebElement jobseekerPassword = d.findElement(By.id("jobseekerPassword"));
		WebElement jobSeekerRegister = d.findElement(By.id("jobSeekerRegister"));
		Assert.assertEquals(JobSeekerName.isDisplayed(), true);
		Reporter.log("Clicked on Register now and inside Registration page", true);
		
		//Entering all the required data and clicking on register
		JobSeekerName.sendKeys(Name);
		jobseekerMobileOrEmail.sendKeys(Email); 
		desiredcategory.sendKeys(Preference1);
		userSecondPreferredCategory.sendKeys(Preference2);
		jobseekerPassword.sendKeys(Password);
		jobSeekerRegister.click();
		
		Thread.sleep(6000);
		
		//Verify is the Newly Registered user has been taken into the logged in User page
		WebElement resendEmailButton = d.findElement(By.id("resendEmailButton"));
		Assert.assertEquals(resendEmailButton.isDisplayed(), true);
		Reporter.log("User now is Registered and inside his page", true);

	}
	
	@Test(priority = 2)
	public void VerifyEmail() throws Exception
	{
		
		Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", Email,Password);
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        

        System.out.println("Total Message:" + folder.getMessageCount());
        System.out.println("Unread Message:"
                + folder.getUnreadMessageCount());
        
        Message[] messages = null;
        boolean isMailFound = false;
        Message mailFromBaba= null;
        
        for (int i = 0; i < 5; i++){messages = folder.search(new SubjectTerm(
                "Babajob.com Email Verification"),
                folder.getMessages());
        //Wait for 10 seconds
        if (messages.length == 0) {
            Thread.sleep(10000);
        }
    }
        
      //Search for unread mail from the set user email
        //This is to avoid using the mail for which 
        //Registration is already done
        for (Message mail : messages) {
            if (!mail.isSet(Flags.Flag.SEEN)) {
            	mailFromBaba = mail;
                System.out.println("Message Count is: "
                        + mailFromBaba.getMessageNumber());
                isMailFound = true;
            }
        }
        
      //Test fails if no unread mail was found from God
        if (!isMailFound) {
            throw new Exception(
                    "Could not find new mail from BabaJob :-(");
        
        //Read the content of mail and launch registration URL                
        } else {
            String line;
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(mailFromBaba
                            .getInputStream()));
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer);

            //Your logic to split the message and get the Registration URL goes here
            String registrationURL = buffer.toString().split("http://www.babajob.com/go/email?value=")[0]
                    .split("href=")[1];
            
           // http://www.babajob.com/go/email?value=nYgRxvIZMwhtUhbRI/+7MdUfN1NKIOMvaCzSeyPliiyxj/fBzJLCaOCjUwYD42E1/0mhdgZJf9P6iIIkRRHx6cyj7Yds4MZSpFF4xqaIHzz8Rn5d/rwFsSvYugitgABpEFEeFUA70LuhrT5YkoP5aYVoJZHWMCaNHZzY+5QvffgYHcdzQNqhqSrPixctPrk98z4Sj5S0CGPvvEpfpRWUuX2n0crveQhPyQ1Xt4IhvhHKCjuGr+JfAy3d5U2Kqt9kyFRuy6Q2JOH4NLuGVtu5Mdf5Sz63xUwBPwPer8ZlAebYbnorgT9h63NdQHsRLzigbozfMLgzzclsi7YA/LriYyW/roFQf3zUR9Q1EwzvVFA=
            System.out.println(registrationURL);
            d.get(registrationURL); // this would verify the email
            
        }
		
		
		
	}

}
