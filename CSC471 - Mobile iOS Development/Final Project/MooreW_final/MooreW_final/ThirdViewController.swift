//
//  ThirdViewController.swift
//  MooreW_final
//
//  Created by William Moore on 3/16/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit
import MessageUI

class ThirdViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func contactEmail(_ sender: UIButton) {
        if !MFMailComposeViewController.canSendMail() {
                  sendError(ErrorType: "mail")
                  return
              } else {
                  let composeMail = MFMailComposeViewController()
              composeMail.setToRecipients(["steve@stevebhandyman.com"])
                      
                  self.present(composeMail, animated: true, completion: nil)
              }
    }
    
    
    @IBAction func contactPhone(_ sender: UIButton) {
        let digits = "7732635975"
        
        guard let number = URL(string: "tel://" + digits) else {
            sendError(ErrorType: "phone")
            return
        }
        UIApplication.shared.open(number)
    }
    
    //Error Handling
    func sendError(ErrorType: String) {
        switch ErrorType {
        case "phone":
            let alertController = UIAlertController(title: "Email Unavailable",
                                                    message: "Phone services are currently unavailable. Please set up a valid phone number on your device or try again later.",
                                                    preferredStyle: .alert);
            
            let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

            alertController.addAction(cancelAction)
            present(alertController, animated: true, completion: nil)
            break
        case "mail":
            let alertController = UIAlertController(title: "Email Unavailable",
                                                    message: "Email services are currently unavailable. Please set up a valid email on your device or try again later.",
                                                    preferredStyle: .alert);
            
            let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

            alertController.addAction(cancelAction)
            present(alertController, animated: true, completion: nil)
            break
        default:
            let alertController = UIAlertController(title: "Service Unavailable",
                                                    message: "Service unavailable. Please try again later.",
                                                    preferredStyle: .alert);
            
            let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

            alertController.addAction(cancelAction)
            present(alertController, animated: true, completion: nil)
            break
        }
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
