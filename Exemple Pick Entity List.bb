Graphics3D 800,600,0,2
SetBuffer BackBuffer ()

; ------------------------
; Freelook cam var
; ------------------------
Global MXS#,MYS#
Global Cam_Pitch#
Global Cam_Yaw#
Global Cam_UpDown#
Global Cam_LeftRight#
Global Cam_VelX#
Global Cam_VelZ#

;============================================= Types

; TYPE Object
Type Terra_Object
	Field Entity
	Field Px#,Py#,Pz#
	Field Name$
End Type

;=============================================

; Camera
Camera = CreateCamera()
CameraClsColor Camera,128,128,255
CameraRange Camera,0.1,10000

; Light
Light = CreateLight(2)
PositionEntity Light,0,5000,0

;============================================= Creation des objets bidons
; Mes Objets
Terra_Original = CreateCube()
For i = 0 To 400
	T.Terra_Object = New Terra_Object
	T\Entity = CopyEntity (Terra_Original)
	T\Px# = Rnd(-100,100)
	T\Py# = Rnd(-20,20)
	T\Pz# = Rnd(-100,100)
	T\Name$ = "Cube_"+i
	PositionEntity T\Entity,T\Px#,T\Py#,T\Pz#
	ScaleEntity T\Entity,Rnd(2,5),Rnd(2,5),Rnd(2,5)
	EntityColor T\Entity,Rand(255),Rand(255),Rand(255)
	NameEntity T\Entity,Handle(T) ;<------- Envoie le handle dans son name
	EntityPickMode T\Entity,2
Next
FreeEntity Terra_Original
;=============================================


While Not KeyHit(1)

	FreelookSmooth(MouseDown(2),Camera,1.1,0.5,10,0.0)

	;============================================= Pickage des objets
	If CameraPick (Camera,MouseX(),MouseY()) <> False
		T.Terra_Object = Object.Terra_Object(EntityName(PickedEntity()))
	EndIf 

	;=============================================	
	RenderWorld
	If (T <> Null)
		; Cette methode
		Text 0,0,"Name : " + Terra_GetObjectName$(T)
		Text 0,10,"Px : " + Terra_GetObjectPx#(T)
		Text 0,20,"Py : " + Terra_GetObjectPy#(T)
		Text 0,30,"Pz : " + Terra_GetObjectPz#(T)

		; ou directement ca
		Text 0,50,"Name : " + T\Name$
		Text 0,60,"Px : " + T\Px#
		Text 0,70,"Py : " + T\Py#
		Text 0,80,"Pz : " + T\Pz#	
	EndIf
	Flip
Wend

; Libere les objets
For T.Terra_Object = Each Terra_Object
	FreeEntity T\Entity
Next 
Delete Each Terra_Object
End


;=============================================	Exemple de fonction pour avec ses spec
Function Terra_GetObjectName$(T.Terra_Object)
	Return T\Name$
End Function 

Function Terra_GetObjectPx#(T.Terra_Object)
	Return T\Px#
End Function
Function Terra_GetObjectPy#(T.Terra_Object)
	Return T\Py#
End Function
Function Terra_GetObjectPz#(T.Terra_Object)
	Return T\Pz#
End Function







;-----------------------------------------------------
; Gestion du freelook douce
;-----------------------------------------------------
Function FreelookSmooth(Action=True,CamEntity,Velocity#=1.1,Speed#=0.5,Damping#=5,Gravity#=0.0)
	MXS#=MouseXSpeed()/1.5
	MYS#=MouseYSpeed()/1.5
	
	If Action=True
		Cam_Pitch#=Cam_Pitch#+MYS#
		Cam_Yaw#=Cam_Yaw#+MXS#
			
		Cam_UpDown#=Cam_UpDown#+((Cam_Pitch#-Cam_UpDown#)/Damping#)
		Cam_LeftRight#=Cam_LeftRight#+((Cam_Yaw#-Cam_LeftRight#)/Damping#)
				
		RotateEntity CamEntity,Cam_UpDown#,-Cam_LeftRight#,0
			
		MoveMouse GraphicsWidth()/2, GraphicsHeight()/2

		If KeyDown(203) Cam_VelX#=Cam_VelX#-Speed# ElseIf KeyDown(205) Cam_VelX#=Cam_VelX#+Speed#
		If KeyDown(208) Cam_VelZ#=Cam_VelZ#-Speed# ElseIf KeyDown(200) Cam_VelZ#=Cam_VelZ#+Speed#
	
		Cam_VelX#=Cam_VelX#/Velocity#
		Cam_VelZ#=Cam_VelZ#/Velocity#

		MoveEntity CamEntity,Cam_VelX#,0,Cam_VelZ#

		; ----------------------------
		; Gestion de la gravité
		; ----------------------------
		TranslateEntity CamEntity,0,Gravity#,0
	EndIf
End Function



