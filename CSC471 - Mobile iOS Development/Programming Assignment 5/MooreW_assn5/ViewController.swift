//
//  ViewController.swift
//  MooreW_assn5
//
//  Created by William Moore on 2/21/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    var items: [String] = []
    var message = ""
    
    @IBOutlet var textFields: [UITextField]!
    
    @IBOutlet weak var textView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    
    @IBAction func editEnded(_ sender: UITextField) {
        sender.resignFirstResponder()
    }
    
    
    @IBAction func backgroundTouched(_ sender: UIControl) {
        for tf in textFields {
            tf.resignFirstResponder()
        }
        textView.resignFirstResponder()
    }
    
    @IBAction func addItem(_ sender: UIButton) {
        var isInputEmpty: Bool = false
        
        for tf in textFields {
            if tf.text!.isEmpty {
                isInputEmpty = true
                sendError()
            }
        }
        
        if !isInputEmpty {
        
            /*Initialize empty dictionary to keep track of text field and their tags
              Used from sample code in class
              */
            var input: [Int:String] = [:]
            for tf in textFields {
                tf.resignFirstResponder()
                
                /*tf.tag is the text field we specify which is then stored in the dictionary
                  tf.text is the String? slot of what we entered in the text field, which needs unwrapping
                  we use the ?? operator with a default value of nothing*/
                input[tf.tag] = tf.text ?? ""
            }
            
            //The message to be displayed in the textView
            if let text = input[1] {
                message += "\(text)x \(input[0] ?? "")\n"
                items.append(message)
            }
            
            //Add to "list"
            for i in items {
                textView.text = i
            }
            
            //Clear the user input for convenience
            for tf in textFields {
                tf.text = ""
            }
        } else {
            sendError()
        }
    }
    
    @IBAction func clearTextFields(_ sender: UIButton) {
        for tf in textFields {
            tf.text = ""
        }
    }
    
    @IBAction func clearList(_ sender: UIButton) {
        if items.count != 0 {
            items.removeAll();
        }
        textView.text = "No Items"
        message = ""
    }
    
    //Alert capabilities in case text input is empty
    func sendError() {
        
        let alertController = UIAlertController(title: "Invalid Input",
                                                message: "One or more text fields are empty",
                                                preferredStyle: .alert);
        
        let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil);

        alertController.addAction(cancelAction)
        present(alertController, animated: true, completion: nil)
    }
    
    
}

