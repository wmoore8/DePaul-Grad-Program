//
//  DVRViewController.swift
//  MooreW_assn6
//
//  Created by William Moore on 3/7/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit

class DVRViewController: UIViewController {
    
    var isOff: Bool = true
    var isPlaying: Bool = false
    var isRecording : Bool = false
    var isStopped : Bool = true

    @IBOutlet weak var powerResult: UILabel!
    @IBOutlet weak var stateResult: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func switchToTV(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func powerSwitch(_ sender: UISwitch) {
        if sender.isOn {
            isOff = false
            powerResult.text = "On"
            stateResult.text = "Stopped"
        } else if !sender.isOn {
            isOff = true
            stateUpdater("Stop")
            powerResult.text = "Off"
        }
    }
    
    
    @IBAction func stateRequest(_ sender: UIButton) {
        if !isOff {
            if let buttonPressed = sender.currentTitle {
                switch buttonPressed {
                case "Play":
                    if !isRecording {
                        stateUpdater("Play")
                    } else {
                        sendError(action: "Play")
                    }
                    break
                case "Stop":
                    stateUpdater("Stop")
                    break
                case "Pause":
                    if isPlaying && !isRecording {
                        stateUpdater("Pause")
                    } else {
                        sendError(action: "Pause")
                    }
                    break
                case "Fast Forward":
                    if isPlaying && !isRecording {
                        stateUpdater("Fast Forward")
                    } else {
                        sendError(action: "Fast Forward")
                    }
                    break
                case "Record":
                    if isStopped {
                        stateUpdater("Record")
                    } else {
                        sendError(action: "Record")
                    }
                    break
                case "Fast Rewind":
                    if isPlaying && !isRecording {
                        stateUpdater("Fast Rewind")
                    } else {
                        sendError(action: "Fast Rewind")
                    }
                    break
                default:
                    break
                }
            }
        }
        
        
        
    }
    
    func stateUpdater(_ buttonAction: String) {
        switch buttonAction {
        case "Play":
            
            isPlaying = true
            isStopped = false
            
            stateResult.text = "Playing"
            break
        
        //The only case that sets isRecording to false
        case "Stop":
            
            isPlaying = false
            isRecording = false
            isStopped = true
            
            stateResult.text = "Stopped"
            break
            
        case "Pause":
            
            isPlaying = true
            isStopped = false
            
            stateResult.text = "Paused"
            break
            
        case "Fast Forward":
            
            isPlaying = true
            isStopped = false
            
            stateResult.text = "Fast Fowarding"
            break
            
        case "Record":
            
            isPlaying = false
            isRecording = true
            isStopped = false
            
            stateResult.text = "Recording"
            break
            
        case "Fast Rewind":
            
            isPlaying = true
            isStopped = false
            
            stateResult.text = "Rewinding"
            break
            
        default:
            break
        }
    }
    
    func sendError(action: String? = nil) {
        
            
        let alertController = UIAlertController(title: "Invalid Operation",
                                                message: "The action you requested is unavailable in the current state",
                                                preferredStyle: .alert);
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        
        let forceAction = UIAlertAction(title: "Force Action", style: .destructive) {
            
            _ in self.isStopped = true
            self.stateUpdater(action ?? "Stop")
            
        }
        

        alertController.addAction(cancelAction)
        alertController.addAction(forceAction)
        
        present(alertController, animated: true, completion: nil)
        
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
