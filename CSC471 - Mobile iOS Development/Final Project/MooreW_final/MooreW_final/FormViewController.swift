//
//  FormViewController.swift
//  MooreW_final
//
//  Created by William Moore on 3/13/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit
import MessageUI

class FormViewController: UIViewController {
    
    @IBOutlet var textField: [UITextField]!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    //Exit segue
    @IBAction func backToHome(_ sender: UIButton) {
        dismiss(animated: true, completion: nil)
    }
    
    //Send email with information filled out in form
    @IBAction func sendEmail(_ sender: UIButton) {
        
        //Send error if all fields are not filled out or string is not detected
        for tf in textField {
            if tf.text?.isEmpty ?? true {
                sendError()
                return
            }
        }
        
        let nameInfo = textField[0].text
        let phoneInfo = textField[1].text
        let emailInfo = textField[2].text
        let descriptionInfo = textField[3].text
        
        //Actual sending mail part
        if !MFMailComposeViewController.canSendMail() {
           sendMailError()
           return
       } else {
            let composeMail = MFMailComposeViewController()
            
            composeMail.setToRecipients(["steve@stevebhandyman.com"])
            composeMail.setSubject(nameInfo! + " Service Request")
            composeMail.setMessageBody(descriptionInfo! + "\nPhone number is: " + phoneInfo! + "\nEmail is: " + emailInfo!, isHTML: false)
            
               
            self.present(composeMail, animated: true, completion: nil)
       }
    }
    
    //Exit text input via touching "next"
    @IBAction func editEnded(_ sender: UITextField) {
        
        sender.resignFirstResponder()
    
        /*TODO: Look up how to click next and immediately go
        to next text entry field
         
         TODO: Have information in form actually ready to ship
         
         TODO: Have window zoom to text input area
        
         TODO: (Maybe) Service Category Picker*/
        
        
    }
    
    //Exit text input via touching background TODO: NOT WORKING
    @IBAction func backgroundTouched(_ sender: UIControl) {
        for tf in textField {
            tf.resignFirstResponder()
        }
        
    }
    @IBAction func otherBackgroundTouched(_ sender: UITapGestureRecognizer) {
        for tf in textField {
            tf.resignFirstResponder()
        }
    }
    
    func sendError() {

        let alertController = UIAlertController(title: "Empty Fields",
                                                message: "Please make sure all fields are filled out and try again later.",
                                                preferredStyle: .alert);
        
        let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

        alertController.addAction(cancelAction)
        present(alertController, animated: true, completion: nil)
            
        
    }
    
    func sendMailError() {

        let alertController = UIAlertController(title: "Email Unavailable",
                                                message: "Email services are currently unavailable. Please set up a valid email on your device or try again later.",
                                                preferredStyle: .alert);
        
        let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

        alertController.addAction(cancelAction)
        present(alertController, animated: true, completion: nil)
            
        
    }

}
