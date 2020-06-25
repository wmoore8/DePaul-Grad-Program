//
//  ViewController.swift
//  MooreW_final
//
//  Created by William Moore on 3/11/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

/*TODO: Change header image to Steve's launch screen logo. */

import UIKit
import MessageUI

class ViewController: UIViewController {
    
    var firstView : UIImageView!
    var secondView: UIImageView!
    
    @IBOutlet weak var stevePortraitImageView: UIImageView!
    
    @IBOutlet weak var kitchenBeforeImageView: UIImageView!
    
    @IBOutlet weak var kitchenSplashBeforeImageView: UIImageView!
    
    @IBOutlet weak var fireplaceBeforeImageView: UIImageView!
    
    @IBOutlet weak var kitchenMattBeforeImageView: UIImageView!
    
    var isKitchenFlipped: Bool = false
    var isKitchenSplashFlipped: Bool = false
    var isFireplaceFlipped: Bool = false
    var isKitchenMattFlipped: Bool = false
    
    var imageCoordinator: [UIImage: String] = [
        #imageLiteral(resourceName: "kitchenBefore"):"kitchenAfter", #imageLiteral(resourceName: "fireplaceBefore"):"fireplaceAfter", #imageLiteral(resourceName: "kitchenSplashBefore"):"kitchenSplashAfter", #imageLiteral(resourceName: "kitchenMattBefore"):"kitchenMattAfter"
    ]
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        startUpImages()
    }
    
    //Clean single function to give image views borders on startup
    func startUpImages() {
        
        //Border images for work done
        startUpImageHelper(kitchenBeforeImageView)
        startUpImageHelper(kitchenSplashBeforeImageView)
        startUpImageHelper(kitchenMattBeforeImageView)
        startUpImageHelper(fireplaceBeforeImageView)
        
        
    }
    
    //Way too many lines of code if I did each image separately
    private func startUpImageHelper(_ imageToBorder: UIImageView) {
        imageToBorder.layer.masksToBounds = true
        imageToBorder.layer.borderWidth = 1.0
        imageToBorder.layer.borderColor = UIColor.orange.cgColor
    }
    
    //Banner button functions
    
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
            let alertController = UIAlertController(title: "Phone Unavailable",
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
    
    
    // The rest of this page handles the flippable images
    
    @IBAction func handleTap(_ gesture: UITapGestureRecognizer) {
        
        gesture.numberOfTapsRequired = 1
        
        firstView = gesture.view as? UIImageView
        
        //Uses image's accesibility label to identify
        let firstViewImageLiteral = #imageLiteral(resourceName: firstView.accessibilityLabel!)
        
        //Find name of image
        let firstImageName = firstView.accessibilityLabel
        
        guard let secondImageName = imageCoordinator[firstViewImageLiteral] else {
            print("Error assigning second image")
            return}
        
        
        flip(flip: firstImageName!, to: secondImageName)
    }
    
    
    /* The following function changes the images based on the image that was tapped.
     
        In order for this to work, thec piture in the stack view MUST:
        1. accessibilityLabel == name of image
        2. Has it's own unique tap gesture recognizer
        3. Has unique boolean instantiated to false
        4. Must be in the imageCoordinator dictionary with the value being it's after image couternpart's name
        5. The image view itself must have "User Interaction Enabled" checked
     */
   
    func flip(flip: String, to: String) {
        
        switch to {
            
        case "kitchenAfter":
            if(!isKitchenFlipped){
                kitchenBeforeImageView.image = UIImage(imageLiteralResourceName: to)
            } else {
                kitchenBeforeImageView.image = UIImage(imageLiteralResourceName: flip)
            }
            isKitchenFlipped = !isKitchenFlipped
            break
            
        case "kitchenSplashAfter":
            if(!isKitchenSplashFlipped){
                kitchenSplashBeforeImageView.image = UIImage(imageLiteralResourceName: to)
            } else {
                kitchenSplashBeforeImageView.image = UIImage(imageLiteralResourceName: flip)
            }
            isKitchenSplashFlipped = !isKitchenSplashFlipped
            break
            
        case "fireplaceAfter":
            if(!isFireplaceFlipped){
                fireplaceBeforeImageView.image = UIImage(imageLiteralResourceName: to)
            } else {
                fireplaceBeforeImageView.image = UIImage(imageLiteralResourceName: flip)
            }
            isFireplaceFlipped = !isFireplaceFlipped
            break
            
        case "kitchenMattAfter":
            if(!isKitchenMattFlipped){
                kitchenMattBeforeImageView.image = UIImage(imageLiteralResourceName: to)
            } else {
                kitchenMattBeforeImageView.image = UIImage(imageLiteralResourceName: flip)
            }
            isKitchenMattFlipped = !isKitchenMattFlipped
            break
        default:
            print("Invalid image flip case")
            return
        }
        
    }
    
    
}

