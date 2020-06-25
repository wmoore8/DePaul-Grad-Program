//
//  ViewController.swift
//  MooreW_assn4
//
//  Created by William Moore on 2/17/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    var volume: Float = 50
    var isOff: Bool = true
    var channelHelper: String? = ""
    
    @IBOutlet weak var powerResult: UILabel!
    @IBOutlet weak var volumeResult: UILabel!
    @IBOutlet weak var channelResult: UILabel!
    
    
    @IBAction func powerSwitch(_ sender: UISwitch) {
        if sender.isOn {
            isOff = false
            powerResult.text = "ON"
            
        } else if !sender.isOn {
            isOff = true
            powerResult.text = "OFF"
        }
    }
    
    @IBAction func volumeSlider(_ sender: UISlider) {
        if !isOff {
            if sender.value != volume * 100 {
                volume = (sender.value * 100).rounded()
                volumeResult.text = volume.description
            }
        }
    }
    
    @IBAction func channelNumber(_ sender: UIButton) {
        if !isOff {
            if channelHelper!.count < 2 {
                if let toUser = sender.currentTitle{
                    channelHelper!.append(toUser)
                    channelResult.text = channelHelper
                }
                
            } else if channelHelper!.count >= 2 {
                channelHelper = ""
                if let toUser = sender.currentTitle{
                    channelHelper!.append(toUser)
                    channelResult.text = channelHelper
                }
            }
        }
    }
    
    
    @IBAction func channelAction(_ sender: UIButton) {
        if !isOff {
            if sender.currentTitle == "+" {
                if var tempVar = Int(channelHelper!) {
                    tempVar += 1
                    channelHelper = tempVar.description
                    channelResult.text = tempVar.description
                }
            } else if sender.currentTitle == "-" {
                if var tempVar = Int(channelHelper!) {
                    
                    //We don't want negative channels
                    if tempVar != 0 {
                        tempVar -= 1
                        channelHelper = tempVar.description
                        channelResult.text = tempVar.description
                    }
                }
            }
        }
    }
    
    @IBAction func favoritesAction(_ sender: UISegmentedControl) {
        if !isOff {
            if let name = sender.titleForSegment(at: sender.selectedSegmentIndex) {
                switch name {
                case "NBC":
                    channelHelper = "17"
                    channelResult.text = channelHelper
                case "ABC":
                    channelHelper = "12"
                    channelResult.text = channelHelper
                case "FOX":
                    channelHelper = "53"
                    channelResult.text = channelHelper
                case "DISNEY":
                    channelHelper = "24"
                    channelResult.text = channelHelper
                default:
                    channelResult.text = channelHelper
                }
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }


}

